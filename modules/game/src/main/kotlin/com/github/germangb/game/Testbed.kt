package com.github.germangb.game

import com.github.germangb.engine.animation.AnimationState
import com.github.germangb.engine.animation.SampledAnimationController
import com.github.germangb.engine.animation.SimpleAnimationManager
import com.github.germangb.engine.assets.NaiveAssetManager
import com.github.germangb.engine.assets.assets
import com.github.germangb.engine.audio.AudioState.PLAYING
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.TransformMode.ABSOLUTE
import com.github.germangb.engine.framework.components.*
import com.github.germangb.engine.framework.materials.DiffuseMaterial
import com.github.germangb.engine.graphics.CullMode
import com.github.germangb.engine.graphics.DrawMode
import com.github.germangb.engine.graphics.InstanceAttribute.TRANSFORM
import com.github.germangb.engine.graphics.MeshPrimitive
import com.github.germangb.engine.graphics.MeshUsage
import com.github.germangb.engine.graphics.TestFunction.DISABLED
import com.github.germangb.engine.graphics.TestFunction.LESS
import com.github.germangb.engine.graphics.TexelFormat.DEPTH24
import com.github.germangb.engine.graphics.TexelFormat.RGB8
import com.github.germangb.engine.graphics.TextureFilter.NEAREST
import com.github.germangb.engine.graphics.VertexAttribute.*
import com.github.germangb.engine.input.KeyboardKey.*
import com.github.germangb.engine.input.isJustPressed
import com.github.germangb.engine.math.*
import com.github.germangb.engine.plugin.bullet.bullet
import com.github.germangb.engine.plugins.assimp.ANIMATIONS
import com.github.germangb.engine.plugins.assimp.assimp
import com.github.germangb.engine.plugins.debug.debug
import com.github.germangb.engine.plugins.heightfield.heightfield
import com.github.germangb.engine.utils.DummyTexture
import org.intellij.lang.annotations.Language
import java.text.NumberFormat
import java.util.*

class Testbed(val ctx: Context) : Application {
    val assetManager = NaiveAssetManager(ctx)
    val resources = ctx.files.getLocal(".")

    init {
        assetManager.preloadAudio(ctx.files.getLocal("music.ogg"), "music")
        assetManager.preloadAudio(ctx.files.getLocal("click.ogg"), "click", stream = false)
        assetManager.preloadMesh(ctx.files.getLocal("cube.blend"), "cube_mesh", MeshUsage.STATIC, arrayOf(POSITION, NORMAL, UV), arrayOf(TRANSFORM))
        assetManager.preloadTexture(ctx.files.getLocal("cube.png"), "cube_texture", RGB8, NEAREST, NEAREST)
    }

    val fbo = ctx.graphics.createFramebuffer(ctx.graphics.width, ctx.graphics.height, arrayOf(RGB8, DEPTH24), NEAREST, NEAREST)
    val quad = let {
        val vert = ctx.buffers.create(100)
        val index = ctx.buffers.create(100).asIntBuffer()
        vert.asFloatBuffer().put(floatArrayOf(-1f, -1f, +1f, -1f, -1f, +1f, +1f, +1f))
        vert.position(8 * 4).flip()
        index.put(intArrayOf(0, 1, 2, 3)).flip()
        val mesh = ctx.graphics.createMesh(vert, index, MeshPrimitive.TRIANGLE_STRIP, MeshUsage.STATIC, arrayOf(POSITION2))
        vert.clear()
        index.clear()
        ctx.buffers.free(vert)
        ctx.buffers.free(index)
        mesh
    }
    val composite = let {
        @Language("GLSL")
        val vert = """#version 450 core
            layout(location = 0) in vec2 a_position;
            out vec2 v_uv;
            void main() {
                gl_Position = vec4(a_position, 0.0, 1.0);
                v_uv = a_position * 0.5 + 0.5;
            }
        """.trimMargin()
        @Language("GLSL")
        val frag = """#version 450 core
            in vec2 v_uv;
            out vec4 frag_color;
            uniform sampler2D u_texture;
            void main() {
                vec3 color = texture2D(u_texture, v_uv).rgb;
                frag_color = vec4(color * vec3(0.97, 0.95, 0.84), 1.0);
            }
        """.trimMargin()
        ctx.graphics.createShaderProgram(vert, frag)
    }

    val skin = Array(110) { Matrix4() }
    val skinData = ctx.buffers.create(110 * 16 * 4).asMatrix4Buffer()
    val instanceData = ctx.buffers.create(1000000)

    val animationManager = SimpleAnimationManager()
    val outlineSkinShader = let {
        @Language("GLSL")
        val vert = """#version 450 core
            layout(location = 0) in vec3 a_position;
            layout(location = 1) in vec3 a_normal;
            layout(location = 3) in ivec4 a_bones;
            layout(location = 4) in vec4 a_weights;
            uniform mat4 u_projection;
            uniform mat4 u_view;
            uniform mat4 u_skin[110];
            void main () {
                mat4 u_skin_transform = u_skin[a_bones.x] * a_weights.x +
                                        u_skin[a_bones.y] * a_weights.y +
                                        u_skin[a_bones.z] * a_weights.z +
                                        u_skin[a_bones.w] * a_weights.w;

                gl_Position = u_projection * u_view * u_skin_transform * vec4(a_position + a_normal, 1.0);
            }
        """.trimMargin()
        @Language("GLSL")
        val frag = """#version 450 core
            out vec4 frag_color;
            uniform sampler2D u_texture;
            void main() {
                frag_color = vec4(0, 0, 0, 1);
            }
        """.trimMargin()
        ctx.graphics.createShaderProgram(vert, frag)
    }
    val skinShader = let {
        @Language("GLSL")
        val vert = """#version 450 core
            layout(location = 0) in vec3 a_position;
            layout(location = 1) in vec3 a_normal;
            layout(location = 2) in vec2 a_uv;
            layout(location = 3) in ivec4 a_bones;
            layout(location = 4) in vec4 a_weights;
            out vec2 v_uv;
            out vec3 v_normal;
            uniform mat4 u_projection;
            uniform mat4 u_view;
            uniform mat4 u_skin[110];
            void main () {
                mat4 u_skin_transform = u_skin[a_bones.x] * a_weights.x +
                                        u_skin[a_bones.y] * a_weights.y +
                                        u_skin[a_bones.z] * a_weights.z +
                                        u_skin[a_bones.w] * a_weights.w;
                mat4 model = u_skin_transform;
                gl_Position = u_projection * u_view * model * vec4(a_position, 1.0);
                v_normal = normalize((model * vec4(a_normal, 0.0)).xyz);
                v_uv = a_uv;
            }
        """.trimMargin()
        @Language("GLSL")
        val frag = """#version 450 core
            in vec2 v_uv;
            in vec3 v_normal;
            out vec4 frag_color;
            uniform sampler2D u_texture;
            void main() {
                float light = clamp(dot(v_normal, normalize(vec3(1, 0, 2))), 0.0, 1.0);
                vec4 color = texture(u_texture, v_uv);
                color *= mix(0.5, 1, smoothstep(0.0, 1.0, light));
                frag_color = vec4(color.rgb, 1.0);
            }
        """.trimMargin()
        ctx.graphics.createShaderProgram(vert, frag)
    }
    val staticShader = let {
        @Language("GLSL")
        val vert = """#version 450 core
            layout(location = 0) in vec3 a_position;
            layout(location = 1) in vec3 a_normal;
            layout(location = 2) in vec2 a_uv;
            layout(location = 3) in mat4 a_instance;
            //layout(location = 4) in vec2 a_uv_offset;
            out vec2 v_uv;
            out vec3 v_normal;
            out vec3 v_position;
            uniform mat4 u_projection;
            uniform mat4 u_view;
            void main () {
                vec4 viewPos = u_view * a_instance * vec4(a_position, 1.0);
                gl_Position = u_projection * viewPos;
                v_normal = normalize((a_instance * vec4(a_normal, 0.0)).xyz);
                v_uv = a_uv;// + a_uv_offset;
                v_position = viewPos.xyz;
            }
        """.trimMargin()
        @Language("GLSL")
        val frag = """#version 450 core
            in vec2 v_uv;
            in vec3 v_normal;
            in vec3 v_position;
            out vec4 frag_color;
            uniform sampler2D u_texture;
            void main() {
                float light = clamp(dot(v_normal, normalize(vec3(1, 0, 2))), 0.0, 1.0);
                vec4 color = texture(u_texture, v_uv);
                color *= mix(0.5, 1, smoothstep(0.0, 1.0, light));
                // fog
                color.rgb = mix(color.rgb, vec3(0.2), clamp(abs(v_position.z) / 32 - 0.1, 0, 0.8));
                frag_color = vec4(color.rgb, 1.0);
            }
        """.trimMargin()
        ctx.graphics.createShaderProgram(vert, frag)
    }
    val root = Actor()
    val animation by lazy {
        // load assimp scene with skinned mesh
        val file = ctx.files.getLocal("hellknight.md5mesh")
        val build = ctx.assimp.loadScene(file, assetManager)?.actor

        // create scene
        val actor = build?.let {
            root.addChild {
                it()
                transform.local.scale(0.05f)
            }
        }

        val animFile = ctx.files.getLocal("idle2.md5anim")
        ctx.assimp.loadScene(animFile, assetManager, ANIMATIONS)?.let { (_, anims, _) ->
            val ai = anims[0]
            val sampled = SampledAnimationController(actor ?: root, ai.frames - 1, ai.fps, ai.timeline)
            val anim = animationManager.createAnimation(sampled)
            anim
        }
    }
    val world = ctx.bullet.createWorld(Vector3(0f, -9.8f, 0f))
    val cube = assetManager.getMesh("cube_mesh")
    val music = assetManager.getAudio("music")
    val click = assetManager.getAudio("click")
    var debug = false

    val floorTexture = ctx.assets.loadTexture(ctx.files.getLocal("dirt.jpg"), RGB8, NEAREST, NEAREST)
    val floorMesh = let {
        val vertex = ctx.buffers.create(1000)
        val index = ctx.buffers.create(1000).asIntBuffer()
        val s = 32f
        vertex.putFloat(-s).putFloat(0f).putFloat(-s)
        vertex.putFloat(0f).putFloat(1f).putFloat(0f)
        vertex.putFloat(0f).putFloat(0f)

        vertex.putFloat(+s).putFloat(0f).putFloat(-s)
        vertex.putFloat(0f).putFloat(1f).putFloat(0f)
        vertex.putFloat(1f).putFloat(0f)

        vertex.putFloat(-s).putFloat(0f).putFloat(+s)
        vertex.putFloat(0f).putFloat(1f).putFloat(0f)
        vertex.putFloat(0f).putFloat(1f)

        vertex.putFloat(+s).putFloat(0f).putFloat(+s)
        vertex.putFloat(0f).putFloat(1f).putFloat(0f)
        vertex.putFloat(1f).putFloat(1f).flip()
        index.put(intArrayOf(0, 1, 2, 3)).flip()
        val mesh = ctx.graphics.createMesh(vertex, index, MeshPrimitive.TRIANGLE_STRIP, MeshUsage.STATIC, arrayOf(POSITION, NORMAL, UV), arrayOf(TRANSFORM))
        vertex.clear()
        index.clear()
        ctx.buffers.free(vertex)
        ctx.buffers.free(index)
        mesh
    }

    val hmap = let {
        val file = ctx.files.getLocal("heightfield.png")
        ctx.heightfield.load16(file, 1, true)
    }

    val hmapMesh = let {
        val size = 16
        val vert = ctx.buffers.create(100000)
        val indx = ctx.buffers.create(100000).asIntBuffer()

        for (x in 0 until size + 1) {
            for (z in 0 until size + 1) {
                vert.putFloat(x.toFloat())
                vert.putFloat(z.toFloat())
            }
        }

        for (z in 0 until size) {
            for (x in 0 until size) {
                val offset = z * size + z
                indx.put(offset + x)
                indx.put(offset + x + 1 + size + 1)
                indx.put(offset + x + size + 1)
                indx.put(offset + x)
                indx.put(offset + x + 1 + size + 1)
                indx.put(offset + x + 1)
            }
        }

        vert.flip()
        indx.flip()
        val mesh = ctx.graphics.createMesh(vert, indx, MeshPrimitive.TRIANGLES, MeshUsage.STATIC, arrayOf(POSITION2), arrayOf(TRANSFORM))
        vert.clear()
        indx.clear()
        ctx.buffers.free(vert)
        ctx.buffers.free(indx)
        mesh
    }

    val hmapShader = let {
        @Language("GLSL")
        val vert = """#version 450 core
            layout(location = 0) in vec2 a_position;
            layout(location = 1) in mat4 a_transform;
            out vec2 v_uv;
            out vec3 v_position;
            uniform mat4 u_view;
            uniform mat4 u_proj;
            uniform float u_size;
            uniform sampler2D u_height;
            void main() {
                vec4 world = a_transform * vec4(a_position.x, 0.0, a_position.y, 1.0);
                v_uv = world.xz / u_size + 0.5;
                //world.y = texture(u_height, v_uv).r * 4;
                vec4 view_pos = u_view * world;
                v_position = view_pos.xyz;
                gl_Position = u_proj * view_pos;
            }
        """.trimMargin()
        @Language("GLSL")
        val frag = """#version 450 core
            in vec2 v_uv;
            in vec3 v_position;
            out vec4 frag_color;
            uniform sampler2D u_texture;
            void main() {
                vec3 color = texture(u_texture, v_uv * 32).rgb;
                color.rgb = mix(color.rgb, vec3(0.2), clamp(abs(v_position.z) / 32 - 0.1, 0, 0.8));
                frag_color = vec4(color, 1.0);
            }
        """.trimMargin()
        ctx.graphics.createShaderProgram(vert, frag)
    }

    override fun destroy() {
        hmapMesh.destroy()
        hmapShader.destroy()
        hmap?.destroy()
        composite.destroy()
        floorMesh.destroy()
        floorTexture?.destroy()
        fbo.destroy()
        ctx.buffers.free(instanceData)
        ctx.buffers.free(skinData)
        skinShader.destroy()
        outlineSkinShader.destroy()
        assetManager.destroy()
        staticShader.destroy()
        cube?.destroy()
        world.destroy()
    }

    override fun init() {
        println(hmap)
        val floor = ctx.bullet.createBox(Vector3(16f, 0.02f, 16f))
        world.createBody(floor, false, 0f, 0.5f, 0f, Matrix4())

        root.addChild {
            val mat = DiffuseMaterial()
            addMeshInstancer(cube!!, mapOf(
                    "diffuse" to (assetManager.getTexture("cube_texture") ?: DummyTexture)
            ))

            addChild {
                transformMode = ABSOLUTE
                transform.local.translate(-4f, 12f, 2f)
                transform.local.rotateX(2f)
                transform.local.rotateZ(0.3f)
                val compShape = ctx.bullet.createCompound()
                compShape.addChild(ctx.bullet.createBox(Vector3(1f)), Matrix4())
                val body = world.createBody(compShape, false, 1f, 0.5f, 0f, transform.local)
                addMeshInstance()
                addUpdate {
                    body.transform.get(transform.local)
                }
            }

            addChild {
                transformMode = ABSOLUTE
                transform.local.translate(-4f, 8f, 2f)
                transform.local.rotateX(2f)
                transform.local.rotateZ(0.3f)
                val compShape = ctx.bullet.createCompound()
                compShape.addChild(ctx.bullet.createBox(Vector3(1f)), Matrix4())
                val body = world.createBody(compShape, false, 1f, 0.5f, 0f, transform.local)
                addMeshInstance()
                addUpdate {
                    body.transform.get(transform.local)
                }
            }

            addChild {
                transformMode = ABSOLUTE
                transform.local.translate(-4f, 4f, 2f)
                transform.local.rotateX(2f)
                transform.local.rotateZ(0.3f)
                val boxShape = ctx.bullet.createBox(Vector3(1f))
                val body = world.createBody(boxShape, false, 1f, 0.5f, 0f, transform.local)
                addMeshInstance()
                addUpdate {
                    body.transform.get(transform.local)
                }
            }

            addChild {
                transformMode = ABSOLUTE
                transform.local.translate(0f, 4f, 4f)
                transform.local.rotateX(0.8f)
                transform.local.rotateZ(0.3f)
                val boxShape = ctx.bullet.createBox(Vector3(1f))
                val body = world.createBody(boxShape, false, 1f, 0.5f, 0f, transform.local)
                addMeshInstance()
                addUpdate {
                    body.transform.get(transform.local)
                }
            }

            addChild {
                transformMode = ABSOLUTE
                transform.local.translate(0f, 4f, -4f)
                transform.local.rotateX(0.8f)
                transform.local.rotateZ(0.3f)
                val boxShape = ctx.bullet.createBox(Vector3(1f))
                val body = world.createBody(boxShape, false, 1f, 0.5f, 0f, transform.local)
                addMeshInstance()
                addUpdate {
                    body.transform.get(transform.local)
                }
            }

            addChild {
                transformMode = ABSOLUTE
                transform.local.translate(0f, 8f, -4f)
                transform.local.rotateX(0.8f)
                transform.local.rotateZ(0.3f)
                val boxShape = ctx.bullet.createBox(Vector3(1f))
                val body = world.createBody(boxShape, false, 1f, 0.5f, 0f, transform.local)
                addMeshInstance()
                addUpdate {
                    body.transform.get(transform.local)
                }
            }
        }

        animation?.play(true)

        // test filed
        println("file = ${resources.path}, dir = ${resources.isDirectory}")
        val children = resources.children
        children.forEach {
            if (it.isDirectory) println("child (dir) = ${it.path}")
            //println("    child = ${it.path}")
        }

        ctx.input.keyboard.setListener {
            //println(it.key)
        }
    }

    fun debug() {
        ctx.debug.add {
            appendln("> # textures = ${ctx.graphics.textures.size}")
            appendln("> # meshes = ${ctx.graphics.meshes.size}")
            appendln("> # shaders = ${ctx.graphics.shaderPrograms.size}")
            appendln("> # framebuffers = ${ctx.graphics.framebuffers.size}")
            appendln("-".repeat(16))
        }

        ctx.debug.add {
            val src = ctx.audio.sources
            appendln("> # audio sources = ${src.size}, gain = ${ctx.audio.gain}")
            src.forEachIndexed { index, audio ->
                appendln("> # $index state = ${audio.state}, gain = ${audio.gain}")
            }

            appendln("-".repeat(16))
            appendln("> # animations = ${animationManager.animations.size}")
            animationManager.animations.forEachIndexed { index, anim ->
                val time = NumberFormat.getNumberInstance().format(animation?.time ?: 0)
                appendln("> # $index [state = ${animation?.state}, timer = $time]")
            }
        }

        ctx.debug.add {
            appendln("-".repeat(16))
            appendln("> # Rigid bodies = ${world.bodies.size}")
            world.bodies.forEach {
                val pos = it.transform.getTranslation(Vector3())
                appendln("> ${pos.toString(NumberFormat.getNumberInstance())}")
            }
        }

    }

    override fun update() {
        if (KEY_GRAVE_ACCENT.isJustPressed(ctx.input)) {
            ctx.debug.toggle()
            click?.play()
        }

        debug()

        world.stepSimulation(1 / 60f)
        animationManager.update(1 / 60f)
        root.update()

        //println("animation(s) = ${animation.controller.time}")

        if (KEY_SPACE.isJustPressed(ctx.input)) {
            if (music?.state == PLAYING) music.pause()
            else music?.play()
        }
        if (KEY_O.isJustPressed(ctx.input)) animation?.stop()
        if (KEY_0.isJustPressed(ctx.input)) {
            animation?.stop()
            animation?.play(false)
        }
        if (KEY_P.isJustPressed(ctx.input)) {
            if (animation?.state != AnimationState.PLAYING) animation?.play(true)
            else animation?.pause()
        }

        ctx.graphics(fbo) {
            state.viewPort(0, 0, fbo.width, fbo.height)
            state.depthTest(LESS)
            state.clearColor(0.2f, 0.2f, 0.2f, 1f)
            state.clearColorBuffer()
            state.clearDepthBuffer()
        }

        val offX = ctx.input.mouse.x - ctx.graphics.width / 2f
        val offY = ctx.input.mouse.y - ctx.graphics.height / 2f

        val aspect = ctx.graphics.width.toFloat() / ctx.graphics.height
        val proj = Matrix4().setPerspective(java.lang.Math.toRadians(55.0).toFloat(), aspect, 0.01f, 1000f)
        val view = Matrix4().setLookAt(Vector3(6f, 4.0f + offY * 0.001f, 3f + offX * 0.001f).mul(1.75f), Vector3(0f, 2.25f, 0f), Vector3(0f, 1f, 0f))

        ctx.graphics(fbo) {
            state.cullMode(CullMode.DISABLED)
            state.polygonMode(DrawMode.SOLID)

            val aux = Matrix4()
            instanceData.clear()
            var count = 0
            val len = 16
            for (x in -len until len + 1) {
                for (z in -len until len + 1) {
                    count++
                    aux.m30(x * 16f)
                    aux.m32(z * 16f)
                    aux.get(instanceData).position(16 * 4 * count)
                }
            }
            instanceData.flip()

            renderInstances(hmapMesh, hmapShader, mapOf(
                    "u_proj" to proj,
                    "u_view" to view,
                    "u_size" to (hmap?.size?.toFloat() ?: 1f),
                    "u_texture" to (floorTexture ?: DummyTexture),
                    "u_height" to (hmap?.texture ?: DummyTexture)
            ), instanceData)
            /*renderInstances(floorMesh, staticShader, mapOf(
                    "u_projection" to proj,
                    "u_view" to view,
                    "u_texture" to (floorTexture ?: DummyTexture)
            ), instanceData)*/

            state.polygonMode(DrawMode.SOLID)
        }

        val stack = Stack<Actor>()
        stack.add(root)
        while (stack.isNotEmpty()) {
            val actor = stack.pop()
            actor.getComponent<JointComponent>()?.let {
                skin[it.id]
                        .set(it.actor.transform.world)
                        .mul(it.offset)
            }
            actor.children.forEach {
                stack.push(it)
            }
        }

        skinData.clear()
        skin.forEachIndexed { i, mat4 ->
            mat4.get(skinData)
            skinData.position(i + 1)
        }
        skinData.flip()

        stack.add(root)
        while (stack.isNotEmpty()) {
            val actor = stack.pop()
            actor.getComponent<MeshInstancerComponent>()?.let { inst ->
                ctx.graphics.state.cullMode(CullMode.BACK)

                // create uniforms
                val mat = inst.material
                val uniforms = mapOf(
                        "u_projection" to proj,
                        "u_view" to view,
                        "u_texture" to (mat["diffuse"] ?: DummyTexture))

                // build instancing buffer data
                instanceData.clear()
                inst.actor.children
                        .mapNotNull { it.getComponent<MeshInstanceComponent>() }
                        .forEachIndexed { index, instance ->
                            instance.actor.transform.world.get(instanceData)
                            instanceData.position(16 * 4 * (index + 1))
                        }
                instanceData.flip()

                ctx.graphics(fbo) {
                    // renderInstances instanced meshes
                    renderInstances(inst.mesh, staticShader, uniforms, instanceData)
                }
            }

            actor.getComponent<SkinnedMeshComponent>()?.let { mesh ->
                ctx.graphics.state.cullMode(CullMode.FRONT)

                val mat = mesh.material
                val texture = mat["diffuse"] ?: DummyTexture
                val uniforms = mapOf(
                        "u_projection" to proj,
                        "u_view" to view,
                        "u_texture" to texture,
                        "u_skin" to skinData)

                ctx.graphics(fbo) {
                    state.cullMode(CullMode.BACK)
                    render(mesh.mesh, skinShader, uniforms)

                    state.cullMode(CullMode.FRONT)
                    render(mesh.mesh, outlineSkinShader, uniforms)
                }
            }

            actor.children.forEach { stack.push(it) }
        }

        ctx.graphics {
            state.viewPort(0, 0, ctx.graphics.width, ctx.graphics.height)
            state.clearColor(0f, 0f, 0f, 1f)
            state.depthTest(DISABLED)
            state.clearColorBuffer()
            render(quad, composite, mapOf("u_texture" to fbo.targets[0]))
            //render(quad, composite, mapOf("u_texture" to (hmap?.texture?:DummyTexture)))
        }
    }

}