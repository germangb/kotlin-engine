package com.github.germangb.game

import com.github.germangb.engine.animation.AnimationState
import com.github.germangb.engine.animation.SampledAnimationController
import com.github.germangb.engine.animation.SimpleAnimationManager
import com.github.germangb.engine.assets.NaiveAssetManager
import com.github.germangb.engine.audio.AudioState.PLAYING
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.TransformMode.ABSOLUTE
import com.github.germangb.engine.framework.components.*
import com.github.germangb.engine.graphics.*
import com.github.germangb.engine.graphics.CullingMode.BACK
import com.github.germangb.engine.graphics.CullingMode.FRONT
import com.github.germangb.engine.graphics.InstanceAttribute.TRANSFORM
import com.github.germangb.engine.graphics.TestFunction.DISABLED
import com.github.germangb.engine.graphics.TestFunction.LESS
import com.github.germangb.engine.graphics.TexelFormat.DEPTH24
import com.github.germangb.engine.graphics.TexelFormat.RGB8
import com.github.germangb.engine.graphics.TextureFilter.LINEAR
import com.github.germangb.engine.graphics.TextureFilter.NEAREST
import com.github.germangb.engine.graphics.VertexAttribute.*
import com.github.germangb.engine.input.KeyboardKey.*
import com.github.germangb.engine.input.isJustPressed
import com.github.germangb.engine.math.*
import com.github.germangb.engine.plugin.bullet.RigidBody
import com.github.germangb.engine.plugin.bullet.bullet
import com.github.germangb.engine.plugins.assimp.ANIMATIONS
import com.github.germangb.engine.plugins.assimp.assimp
import com.github.germangb.engine.plugins.debug.debug
import com.github.germangb.engine.plugins.heightfield.terrain
import com.github.germangb.engine.utils.DummyMesh
import com.github.germangb.engine.utils.DummyTexture
import org.intellij.lang.annotations.Language
import java.text.NumberFormat

val WORLD_FLAG = 0x1
val RAGDOLL_FLAG = 0x2
val PROPS_FLAG = 0x4

class Testbed(val ctx: Context) : Application {
    val assetManager = NaiveAssetManager(ctx)
    val resources = ctx.files.getLocal(".")

    init {
        assetManager.preloadAudio(ctx.files.getLocal("audio/birds.ogg"), "music")
        assetManager.preloadAudio(ctx.files.getLocal("audio/click.ogg"), "click", stream = false)
        assetManager.preloadMesh(ctx.files.getLocal("meshes/cube.blend"), "cube_mesh", MeshUsage.STATIC, arrayOf(POSITION, NORMAL, UV), arrayOf(TRANSFORM))
        assetManager.preloadTexture(ctx.files.getLocal("textures/cube.png"), "cube_texture", RGB8, NEAREST, NEAREST)
        assetManager.preloadTexture(ctx.files.getLocal("textures/dirt.jpg"), "dirt_texture", RGB8, LINEAR, LINEAR, true)
    }

    var ragdoll = false

    val shadowFbo = ctx.graphics.createFramebuffer(1024, 1024, arrayOf(DEPTH24))
    val shadowProjection = Matrix4()
    val shadowView = Matrix4()

    val fbo = let {
        val (width, height) = ctx.graphics.dimensions
        ctx.graphics.createFramebuffer(width, height, arrayOf(RGB8, DEPTH24)) { (_, width, height, format) ->
            createTexture(width, height, format, NEAREST, NEAREST)
        }
    }

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
        val shader = """
            #ifdef VERTEX_SHADER
            layout(location = 0) in vec2 a_position;
            out vec2 v_uv;
            void main() {
                gl_Position = vec4(a_position, 0.0, 1.0);
                v_uv = a_position * 0.5 + 0.5;
            }
            #endif
            #ifdef FRAGMENT_SHADER
            in vec2 v_uv;
            out vec4 frag_color;
            uniform sampler2D u_texture;
            void main() {
                vec3 color = texture2D(u_texture, v_uv).rgb;
                frag_color = vec4(color * vec3(0.97, 0.95, 0.82), 1.0);
            }
            #endif
        """.trimMargin()
        ctx.graphics.createShaderProgram(shader)
    }

    val skin = Array(110) { Matrix4() }
    val skinData = ctx.buffers.create(128 * 16 * 4).asFloatBuffer()
    val instanceData = ctx.buffers.create(1000000)

    val animationManager = SimpleAnimationManager()
    val outlineSkinShader = let {
        @Language("GLSL")
        val shader = """
            #ifdef VERTEX_SHADER
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
            #endif
            #ifdef FRAGMENT_SHADER
            out vec4 frag_color;
            uniform sampler2D u_texture;
            void main() {
                frag_color = vec4(0, 0, 0, 1);
            }
            #endif
        """.trimMargin()
        ctx.graphics.createShaderProgram(shader)
    }
    val skinShader = let {
        val vertFile = ctx.files.getLocal("shaders/skin.vert")
        val vert = vertFile.read()?.bufferedReader()?.use { it.readText() } ?: ""
        @Language("GLSL")
        val frag = """
            in vec2 v_uv;
            in vec3 v_normal;
            in vec4 v_shadow_position;

            out vec4 frag_color;

            uniform sampler2D u_texture;
            uniform sampler2D u_depth;

            #define LIGHT_UTILS
            #include "shaders/utils.glsl"

            void main() {
                float light = clamp(dot(v_normal, normalize(vec3(1, 0, 2))), 0.0, 1.0);


                float shadow = shadow_contrib(v_shadow_position.xyz, u_depth);
                light = min(light, shadow);

                light = mix(0.5, 1, light);

                vec4 color = texture(u_texture, v_uv);
                color *= mix(0.5, 1, smoothstep(0.0, 1.0, light));
                frag_color = vec4(color.rgb, 1.0);
            }
        """.trimMargin()
        ctx.graphics.createShaderProgram(vert, frag)
    }
    val staticShader = let {
        @Language("GLSL")
        val shader = """
            #ifdef VERTEX_SHADER
            layout(location = 0) in vec3 a_position;
            layout(location = 1) in vec3 a_normal;
            layout(location = 2) in vec2 a_uv;
            layout(location = 3) in mat4 a_instance;
            //layout(location = 4) in vec2 a_uv_offset;
            out vec2 v_uv;
            out vec3 v_normal;
            out vec3 v_position;
            out vec4 v_shadow_position;

            uniform mat4 u_projection;
            uniform mat4 u_view;
            uniform mat4 u_shadow_viewproj;

            void main () {
                vec4 viewPos = u_view * a_instance * vec4(a_position, 1.0);
                gl_Position = u_projection * viewPos;
                v_shadow_position = u_shadow_viewproj * a_instance * vec4(a_position, 1.0);
                v_normal = normalize((a_instance * vec4(a_normal, 0.0)).xyz);
                v_uv = a_uv;// + a_uv_offset;
                v_position = viewPos.xyz;
            }
            #endif

            #ifdef FRAGMENT_SHADER

            #define FOG_UTILS
            #define LIGHT_UTILS
            #include "shaders/utils.glsl"

            in vec2 v_uv;
            in vec3 v_normal;
            in vec3 v_position;
            in vec4 v_shadow_position;

            out vec4 frag_color;

            uniform sampler2D u_texture;
            uniform sampler2D u_depth;

            void main() {
                float light = clamp(dot(v_normal, normalize(vec3(1, 0, 2))), 0.0, 1.0);

                // compute shader
                float shadow = shadow_contrib(v_shadow_position.xyz, u_depth);
                light = min(light, shadow);

                vec4 color = texture(u_texture, v_uv);
                color *= mix(0.5, 1, smoothstep(0.0, 1.0, light));
                // fog
                color.rgb = mix(color.rgb, vec3(0.2), clamp(abs(v_position.z) / 32 - 0.1, 0, 0.8));
                frag_color = vec4(color.rgb, 1.0);
            }
            #endif
        """.trimMargin()
        ctx.graphics.createShaderProgram(shader)
    }
    val root = Actor()
    val animation by lazy {
        // load assimp scene with skinned mesh
        val file = ctx.files.getLocal("meshes/hellknight.md5mesh")
        val build = ctx.assimp.loadScene(file, assetManager)?.actor

        // create scene
        val actor = build?.let {
            root.attachChild {
                it()
                transform.scale(0.05f)
            }
        }

        val animFile = ctx.files.getLocal("meshes/idle2.md5anim")
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

    val floorTexture = assetManager.getTexture("dirt_texture")

    val hmapHeight = 24f
    val hmap = let {
        val file = ctx.files.getLocal("data/terrain/height.png")
        ctx.terrain.load16(file, 1, true)
    }

    val hmapMesh = let {
        val size = 32
        val vert = ctx.buffers.create(100000)
        val indx = ctx.buffers.create(100000).asIntBuffer()

        for (x in 0 until size + 1) {
            for (z in 0 until size + 1) {
                vert.putFloat(x.toFloat() - size / 2)
                vert.putFloat(z.toFloat() - size / 2)
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
        val file = ctx.files.getLocal("shaders/terrain.glsl")
        val shader = file.read()?.bufferedReader()?.use { it.readText() } ?: ""
        ctx.graphics.createShaderProgram(shader)
    }
    val proj = Matrix4()
    val view = Matrix4()
    val temp = Matrix4()

    override fun destroy() {
        shadowFbo.destroy()
        hmapMesh.destroy()
        hmapShader.destroy()
        hmap?.destroy()
        composite.destroy()
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
        if (hmap == null) {
            val floor = ctx.bullet.createBox(Vector3(16f, 0.02f, 16f))
            val body = world.addRigidBody(floor, 0f, WORLD_FLAG, RAGDOLL_FLAG or PROPS_FLAG)
            body.friction = 0.5f
            body.restitution = 0f
        } else {
            val height = ctx.bullet.createHeightfield(hmap.size, hmap.size, hmap.data, hmapHeight / Short.MAX_VALUE, -10f, 10f)
            val body = world.addRigidBody(height, 0f, WORLD_FLAG, RAGDOLL_FLAG or PROPS_FLAG)
            body.friction = 0.75f
            body.restitution = 0f
        }

        root.attachChild {
            val tex = assetManager.getTexture("cube_texture") ?: DummyTexture
            addMeshInstancer(cube ?: DummyMesh, mapOf("diffuse" to tex))

            fun cube(x: Float, y: Float, z: Float): Actor.() -> Unit = {
                transformMode = ABSOLUTE
                transform.translate(x, y, z)
                transform.rotateX(1f)
                transform.rotateZ(0.2f)
                val compShape = ctx.bullet.createCompound()
                compShape.addChild(ctx.bullet.createBox(Vector3(1f)), Matrix4())
                val body = world.addRigidBody(compShape, 1f, PROPS_FLAG, WORLD_FLAG or PROPS_FLAG)
                body.friction = 0.5f
                body.restitution = 0f
                body.transform = transform
                addMeshInstance()
                addUpdate {
                    body.transform.get(transform)
                }
            }

            attachChild(cube(-4f, 12f, 2f))
            attachChild(cube(-4f, 8f, 2f))
            attachChild(cube(-4f, 4f, 2f))
            attachChild(cube(0f, 4f, 4f))
            attachChild(cube(0f, 4f, -4f))
            attachChild(cube(0f, 8f, -4f))
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
        val str = buildString {
            appendln("> # textures = ${ctx.graphics.textures.size}")
            appendln("> # meshes = ${ctx.graphics.meshes.size}")
            appendln("> # shaders = ${ctx.graphics.shaderPrograms.size}")
            appendln("> # framebuffers = ${ctx.graphics.framebuffers.size}")
            appendln("-".repeat(16))

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

            appendln("-".repeat(16))
            appendln("> # Rigid bodies = ${world.bodies.size}")
            world.bodies.forEach {
                val pos = it.transform.getTranslation(Vector3())
                appendln("> ${pos.toString(NumberFormat.getNumberInstance())}")
            }
        }
        ctx.debug.add(str)
    }

    fun input() {
        if (KEY_UP.isJustPressed(ctx.input)) ctx.debug.fontSize++
        if (KEY_DOWN.isJustPressed(ctx.input)) ctx.debug.fontSize--
        if (KEY_GRAVE_ACCENT.isJustPressed(ctx.input)) {
            ctx.debug.toggle()
            click?.play()
        }

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

        if (KEY_R.isJustPressed(ctx.input)) {
            if (!ragdoll) {

                val bodies = mutableMapOf<Actor, RigidBody>()

                val bfs = root.breadthFirstTraversal()
                bfs.mapNotNull { actor ->
                    actor.joint?.let {
                        actor to it
                    }
                }.forEach { (actor, joint) ->
                    // set transform mode to absolute
                    actor.transform.set(actor.worldTransform)
                    actor.transformMode = ABSOLUTE

                    // create rigid body
                    val shape = ctx.bullet.createBox(Vector3(1f))
                    val body = world.addRigidBody(shape, 1f, RAGDOLL_FLAG, WORLD_FLAG)
                    body.transform = actor.transform

                    bodies[actor] = body

                    // create constraint
                    bodies[actor.parent]?.let {
                        val pivotA = Vector3()
                        val pivotB = Vector3()
                        actor.transform.getTranslation(pivotA)
                        actor.parent.worldTransform.getTranslation(pivotB)
                        val const = ctx.bullet.createPoint2PointContraint(body, it, pivotA, pivotB)
                        world.addConstraint(const)
                    }

                    actor.addUpdate {
                        actor.transform.set(body.transform)
                    }
                }

                ragdoll = true
            }

        }
    }

    fun buildTerrainInstancing(view: Matrix4c, proj: Matrix4c) {
        val culler = FrustumIntersection(temp.set(proj).mul(view))
        val aux = Matrix4()
        instanceData.clear()
        var count = 0
        val len = 16
        val min = Vector3()
        val max = Vector3()
        val aabb = AABB()
        for (x in -len until len + 1) {
            for (z in -len until len + 1) {
                min.set(x * 32f, 0f, z * 32f).add(-16f, -16f, -16f)
                max.set(x * 32f, 0f, z * 32f).add(16f, 16f, 16f)
                aabb.setMin(min)
                aabb.setMax(max)
                if (culler.testAab(aabb)) {
                    count++
                    aux.m30(x * 32f)
                    aux.m32(z * 32f)
                    aux.get(instanceData).position(16 * 4 * count)
                }
            }
        }
        instanceData.flip()
    }

    fun buildInstanceData(root: Actor) {
        instanceData.clear()
        root.children
                .filter { it.instance != null }
                .forEachIndexed { index, actor ->
                    actor.worldTransform.get(instanceData)
                    instanceData.position(16 * 4 * (index + 1))
                }
        instanceData.flip()
    }

    fun render(fbo: Framebuffer, view: Matrix4c, proj: Matrix4c) {
        ctx.graphics(fbo) {
            buildTerrainInstancing(view, proj)
            render(hmapMesh, hmapShader, uniformMap(
                    "u_shadow_viewproj" to Matrix4(shadowProjection).mul(shadowView),
                    "u_depth" to shadowFbo.textures[0],
                    "u_proj" to proj,
                    "u_view" to view,
                    "u_size" to (hmap?.size?.toFloat() ?: 1f),
                    "u_max_height" to hmapHeight,
                    "u_texture" to (floorTexture ?: DummyTexture),
                    "u_height" to (hmap?.texture ?: DummyTexture)
            ), instanceData)
        }

        ctx.graphics(fbo) {
            val trav = root.breadthFirstTraversal()
            trav.forEach { actor ->
                actor.meshInstancer?.let { inst ->
                    ctx.graphics.cullMode(BACK)
                    buildInstanceData(actor)
                    val texture = inst.material["diffuse"] ?: DummyTexture
                    val uniforms = uniformMap(
                            "u_shadow_viewproj" to Matrix4(shadowProjection).mul(shadowView),
                            "u_depth" to shadowFbo.textures[0],
                            "u_projection" to proj,
                            "u_view" to view,
                            "u_texture" to texture)
                    render(inst.mesh, staticShader, uniforms, instanceData)
                }
            }
        }

        ctx.graphics(fbo) {
            val tra = root.breadthFirstTraversal()
            tra.forEach { actor ->
                actor.skinnedMesh?.let { mesh ->
                    ctx.graphics.cullMode(FRONT)
                    val texture = mesh.material["diffuse"] ?: DummyTexture
                    val uniforms = uniformMap(
                            "u_shadow_viewproj" to Matrix4(shadowProjection).mul(shadowView),
                            "u_depth" to shadowFbo.textures[0],
                            "u_projection" to proj,
                            "u_view" to view,
                            "u_texture" to texture,
                            "u_skin" to skinData
                    )
                    cullMode(BACK)
                    render(mesh.mesh, skinShader, uniforms)
                    cullMode(FRONT)
                    render(mesh.mesh, outlineSkinShader, uniforms)
                }
            }
            cullMode(CullingMode.DISABLED)
        }
    }

    override fun update() {
        input()
        debug()

        world.stepSimulation(ctx.time.delta)

        if (!ragdoll) {
            animationManager.update(ctx.time.delta)
        } else {
            // update rigid joints according to rigid bodies
        }

        val bfs = root.breadthFirstTraversal()
        bfs.mapNotNull { it.updater }.forEach { it.update() }

        root.computeTransforms()

        // compute skin transforms
        root.breadthFirstTraversal().forEach { actor ->
            actor.joint?.let {
                skin[it.id].set(actor.worldTransform).mul(it.offset)
            }
        }
        // buid skin uniform data
        skinData.clear()
        skin.forEachIndexed { i, mat4 ->
            mat4.get(skinData)
            skinData.position(16 * i + 16)
        }
        skinData.flip()

        ctx.graphics(shadowFbo) {
            clearDepthBuffer()
        }

        ctx.graphics(fbo) {
            depthTest(LESS)
            clearColor(0.2f, 0.2f, 0.2f, 1f)
            clearColorBuffer()
            clearDepthBuffer()
        }

        val (w, h) = ctx.graphics.dimensions
        val offX = ctx.input.mouse.x - w / 2f
        val offY = ctx.input.mouse.y - h / 2f

        val s = 8f
        shadowProjection.setOrtho(-s, s, -s, s, -s * 2, s * 2)
        shadowView.setLookAt(0f, 0f, 0f, -2f, -3f, -1f, 0f, 1f, 0f)
        render(shadowFbo, shadowView, shadowProjection)

        val aspect = w.toFloat() / h
        proj.setPerspective(java.lang.Math.toRadians(55.0).toFloat(), aspect, 0.01f, 1024f)
        view.setLookAt(Vector3(6f, 4.0f + offY * 0.004f, 3f + offX * 0.004f).mul(1.75f), Vector3(0f, 2.25f, 0f), Vector3(0f, 1f, 0f))
        render(fbo, view, proj)

        ctx.graphics {
            clearColor(0f, 0f, 0f, 1f)
            depthTest(DISABLED)
            cullMode(CullingMode.DISABLED)
            clearColorBuffer()
            render(quad, composite, uniformMap("u_texture" to fbo.textures[0]))
        }
    }

}