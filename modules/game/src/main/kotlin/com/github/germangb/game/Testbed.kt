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
import com.github.germangb.engine.framework.materials.DiffuseMaterial
import com.github.germangb.engine.graphics.CullMode
import com.github.germangb.engine.graphics.DrawMode
import com.github.germangb.engine.graphics.MeshUsage
import com.github.germangb.engine.graphics.TestFunction.LESS
import com.github.germangb.engine.graphics.TexelFormat.RGB8
import com.github.germangb.engine.graphics.TextureFilter.NEAREST
import com.github.germangb.engine.graphics.VertexAttribute.*
import com.github.germangb.engine.input.KeyboardKey.*
import com.github.germangb.engine.input.isJustPressed
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.math.asMatrix4Buffer
import com.github.germangb.engine.math.get
import com.github.germangb.engine.plugin.bullet.bullet
import com.github.germangb.engine.plugins.assimp.ANIMATIONS
import com.github.germangb.engine.plugins.assimp.assimp
import com.github.germangb.engine.plugins.debug.debug
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
        assetManager.preloadMesh(ctx.files.getLocal("cube.blend"), "cube_mesh", MeshUsage.STATIC, POSITION, NORMAL, UV)
        assetManager.preloadTexture(ctx.files.getLocal("cube.png"), "cube_texture", RGB8, NEAREST, NEAREST)
    }

    val skin = Array(110) { Matrix4() }
    val skinData = ctx.buffers.create(110 * 16 * 4).asMatrix4Buffer()

    val animationManager = SimpleAnimationManager()
    val outlineSkinShader = let {
        @Language("GLSL")
        val vert = """#version 450 core
            layout(location = 0) in vec3 a_position;
            layout(location = 1) in vec3 a_normal;
            layout(location = 3) in ivec4 a_bones;
            layout(location = 4) in vec4 a_weights;
            layout(location = 5) in mat4 a_instance;
            uniform mat4 u_projection;
            uniform mat4 u_view;
            uniform mat4 u_skin[110];
            void main () {
                mat4 u_skin_transform = u_skin[a_bones.x] * a_weights.x +
                                        u_skin[a_bones.y] * a_weights.y +
                                        u_skin[a_bones.z] * a_weights.z +
                                        u_skin[a_bones.w] * a_weights.w;

                gl_Position = u_projection * u_view * a_instance * u_skin_transform * vec4(a_position + a_normal, 1.0);
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
            layout(location = 5) in mat4 a_instance;
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
                mat4 model = a_instance * u_skin_transform;
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
            layout(location = 4) in vec2 a_uv_offset;
            out vec2 v_uv;
            out vec3 v_normal;
            uniform mat4 u_projection;
            uniform mat4 u_view;
            void main () {
                gl_Position = u_projection * u_view * a_instance * vec4(a_position, 1.0);
                v_normal = normalize((a_instance * vec4(a_normal, 0.0)).xyz);
                v_uv = a_uv + a_uv_offset;
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
    val root = Actor()
    val animation by lazy {
        // load assimp scene with skinned mesh
        val file = ctx.files.getLocal("hellknight.md5mesh")
        val build = ctx.assimp.loadScene(file, assetManager)?.actor

        // create scene
        val actor = build?.let {
            root.addChild {
                it()
                transform.local.scale(0.025f)
            }
        }

        //println("animations")

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
    var debug = true

    override fun init() {
        val floor = ctx.bullet.createBox(Vector3(16f, 0.02f, 16f))
        world.createBody(floor, false, 0f, 0.5f, 0f, Matrix4())

        root.addChild {
            val mat = DiffuseMaterial()
            mat.diffuse = assetManager.getTexture("cube_texture") ?: DummyTexture
            addMeshInstancer(cube!!, mat)

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
            println("    child = ${it.path}")
        }
    }

    override fun update() {
        if (KEY_D.isJustPressed(ctx.input)) {
            click?.play()
            debug = debug.not()
        }
        if (debug) {
            //ctx.debug?.add("HELLO WORLD!!")
            ctx.debug.add {
                appendln("> # textures = ${ctx.graphics.textures.size}")
                appendln("> # meshes = ${ctx.graphics.meshes.size}")
                appendln("> # shaders = ${ctx.graphics.shaderPrograms.size}")
                appendln("> # framebuffers = ${ctx.graphics.framebuffers.size}")
                appendln("-".repeat(92))
            }

            ctx.debug.add {
                val src = ctx.audio.sources
                appendln("> # audio sources = ${src.size}, gain = ${ctx.audio.gain}")
                src.forEachIndexed { index, audio ->
                    appendln("> # $index state = ${audio.state}, gain = ${audio.gain}")
                }

                appendln("-".repeat(80))
                appendln("> # animations = ${animationManager.animations.size}")
                animationManager.animations.forEachIndexed { index, anim ->
                    val time = NumberFormat.getNumberInstance().format(animation?.time ?: 0)
                    appendln("> # $index [state = ${animation?.state}, timer = $time]")
                }
            }

            ctx.debug.add {
                appendln("-".repeat(80))
                appendln("> # Rigid bodies = ${world.bodies.size ?: 0}")
                world.bodies.forEach {
                    val pos = it.transform.getTranslation(Vector3())
                    appendln("> ${pos.toString(NumberFormat.getNumberInstance())}")
                }
            }
        }

        world.stepSimulation(1 / 60f)
        animationManager.update(1 / 60f)
        root.update()

        //println("animation(s) = ${animation.controller.time}")

        if (KEY_SPACE.isJustPressed(ctx.input)) {
            if (music?.state == PLAYING) music.pause()
            else music?.play()
        }
        if (KEY_S.isJustPressed(ctx.input)) animation?.stop()
        if (KEY_0.isJustPressed(ctx.input)) {
            animation?.stop()
            animation?.play(false)
        }
        if (KEY_P.isJustPressed(ctx.input)) {
            if (animation?.state != AnimationState.PLAYING) animation?.play(true)
            else animation?.pause()
        }

        ctx.graphics.state.clearColor(0.2f, 0.2f, 0.2f, 1f)
        ctx.graphics.state.clearColorBuffer()
        ctx.graphics.state.clearDepthBuffer()
        ctx.graphics.state.depthTest(LESS)

        val offX = ctx.input.mouse.x - ctx.graphics.width / 2f
        val offY = ctx.input.mouse.y - ctx.graphics.height / 2f

        val aspect = ctx.graphics.width.toFloat() / ctx.graphics.height
        val proj = Matrix4().setPerspective(java.lang.Math.toRadians(55.0).toFloat(), aspect, 0.01f, 1000f)
        val view = Matrix4().setLookAt(Vector3(6f, 4.0f + offY * 0.001f, 3f + offX * 0.001f).mul(1f), Vector3(0f, 1.5f, 0f), Vector3(0f, 1f, 0f))

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
                val texture = (mat as? DiffuseMaterial)?.diffuse ?: DummyTexture
                val uniforms = mapOf(
                        "u_projection" to proj,
                        "u_view" to view,
                        "u_texture" to texture)

                ctx.graphics.instancing(inst.mesh, staticShader, uniforms) {
                    inst.actor.children
                            .mapNotNull { it.getComponent<MeshInstanceComponent>() }
                            .forEach {
                                transform.set(it.actor.transform.world)
                                instance()
                            }
                }
            }
            actor.getComponent<SkinnedMeshInstancerComponent>()?.let { inst ->
                ctx.graphics.state.cullMode(CullMode.FRONT)

                val mat = inst.material
                val texture = (mat as? DiffuseMaterial)?.diffuse ?: DummyTexture
                val uniforms = mapOf(
                        "u_projection" to proj,
                        "u_view" to view,
                        "u_texture" to texture,
                        "u_skin" to skinData)

                ctx.graphics.instancing(inst.mesh, outlineSkinShader, uniforms) {
                    actor.children
                            .mapNotNull { it.getComponent<SkinnedMeshInstanceComponent>() }
                            .forEach { instance() }
                }

                ctx.graphics.state.cullMode(CullMode.BACK)

                ctx.graphics.instancing(inst.mesh, skinShader, uniforms) {
                    actor.children
                            .mapNotNull { it.getComponent<SkinnedMeshInstanceComponent>() }
                            .forEach {
                                ctx.graphics.state.polygonMode(DrawMode.SOLID)
                                instance()
                            }

                    ctx.graphics.state.polygonMode(DrawMode.SOLID)
                }
            }

            actor.children.forEach { stack.push(it) }
        }
    }

    override fun destroy() {
        ctx.buffers.free(skinData.container)
        skinShader.destroy()
        outlineSkinShader.destroy()
        assetManager.destroy()
        staticShader.destroy()
        cube?.destroy()
        world.destroy()
    }
}