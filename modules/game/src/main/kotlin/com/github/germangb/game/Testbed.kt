package com.github.germangb.game

import com.github.germangb.engine.animation.*
import com.github.germangb.engine.assets.NaiveAssetManager
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Material
import com.github.germangb.engine.framework.Materialc
import com.github.germangb.engine.framework.TransformMode.ABSOLUTE
import com.github.germangb.engine.framework.components.*
import com.github.germangb.engine.graphics.CullMode
import com.github.germangb.engine.graphics.TestFunction
import com.github.germangb.engine.graphics.TexelFormat.RGB8
import com.github.germangb.engine.graphics.TextureFilter.NEAREST
import com.github.germangb.engine.graphics.VertexAttribute.*
import com.github.germangb.engine.input.KeyboardKey
import com.github.germangb.engine.input.isJustPressed
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Quaternion
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.plugin.bullet.bullet
import com.github.germangb.engine.plugins.assimp.assimp
import com.github.germangb.engine.plugins.debug.debug
import org.intellij.lang.annotations.Language
import java.text.NumberFormat
import java.util.*

val Materialc.diffuse get() = getTexture("diffuse")
val Materialc.normals get() = getTexture("normals")

class Testbed(val ctx: Context) : Application {
    val assetManager = NaiveAssetManager(ctx.assets)

    init {
        assetManager.preloadAudio("music.ogg")
        assetManager.preloadAudio("click.ogg", stream = false)
        assetManager.preloadMesh("cube.blend", setOf(POSITION, NORMAL, UV))
    }

    val animationManager = SimpleAnimationManager()
    val skin = Array<Matrix4c>(110) { Matrix4() }
    val outlineSkinShader = let {
        @Language("GLSL")
        val vert = """#version 450 core
            layout(location = 0) in vec3 a_position;
            layout(location = 1) in vec3 a_normal;
            layout(location = 3) in vec4 a_bones;
            layout(location = 4) in vec4 a_weights;
            layout(location = 5) in mat4 a_instance;
            uniform mat4 u_projection;
            uniform mat4 u_view;
            uniform mat4 u_skin[110];
            void main () {
                mat4 u_skin_transform = u_skin[int(a_bones.x+0.001)] * a_weights.x +
                                        u_skin[int(a_bones.y+0.001)] * a_weights.y +
                                        u_skin[int(a_bones.z+0.001)] * a_weights.z +
                                        u_skin[int(a_bones.w+0.001)] * a_weights.w;

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
            layout(location = 3) in vec4 a_bones;
            layout(location = 4) in vec4 a_weights;
            layout(location = 5) in mat4 a_instance;
            out vec2 v_uv;
            out vec3 v_normal;
            uniform mat4 u_projection;
            uniform mat4 u_view;
            uniform mat4 u_skin[110];
            void main () {
                mat4 u_skin_transform = u_skin[int(a_bones.x+0.001)] * a_weights.x +
                                        u_skin[int(a_bones.y+0.001)] * a_weights.y +
                                        u_skin[int(a_bones.z+0.001)] * a_weights.z +
                                        u_skin[int(a_bones.w+0.001)] * a_weights.w;
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
            out vec2 v_uv;
            out vec3 v_normal;
            uniform mat4 u_projection;
            uniform mat4 u_view;
            void main () {
                gl_Position = u_projection * u_view * a_instance * vec4(a_position, 1.0);
                v_normal = normalize((a_instance * vec4(a_normal, 0.0)).xyz);
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
    val root = Actor()
    val animation by lazy {
        // load assimp scene with skinned mesh
        val build = ctx.assimp?.loadActor("hellknight.md5mesh", assetManager)
        var actor: Actor? = null

        build?.let {
            actor = root.addChild {
                it()
                transform.local.scale(0.025f)
            }
        }

        ctx.assimp?.loadAnimations("idle2.md5anim")?.let { (frames, fps, timeline) ->
            val sampled = SampledAnimationController(actor!!, frames - 1, fps, timeline)
            animationManager.createAnimation(sampled)
        }
    }
    val world = ctx.bullet?.createWorld(Vector3(0f, -9.8f, 0f))
    val cube = assetManager.getMesh("cube.blend")
    val music = assetManager.getAudio("music.ogg")
    val click = assetManager.getAudio("click.ogg")
    var debug = true

    override fun init() {
        val floor = world?.createBox(Vector3(16f, 0.02f, 16f))
        world?.createBody(floor!!, false, 0f, 0.5f, 0f, Matrix4())

//        ctx.input.keyboard.setListener { (key, state) ->
//            if (state == InputState.PRESSED && key.isPrintable)
//                println("$key")
//        }

        ctx.input.mouse.setListener { (button, state) ->
            println("$button $state")
        }

        assetManager.preloadTexture("cube.png", RGB8, NEAREST, NEAREST)

        assetManager.getTexture("cube.png")?.let { tex ->
            root.addChild {
                val mat = Material()
                mat.setTexture("diffuse", tex)
                addMeshInstancer(cube!!, mat)

                addChild {
                    transformMode = ABSOLUTE
                    transform.local.translate(-4f, 12f, 2f)
                    transform.local.rotateX(2f)
                    transform.local.rotateZ(0.3f)
                    val compShape = world?.createCompound()
                    compShape?.addChild(world?.createBox(Vector3(1f))!!, Matrix4())
                    val body = world?.createBody(compShape!!, false, 1f, 0.5f, 0f, transform.local)
                    addMeshInstance()
                    addUpdate {
                        body?.transform?.get(transform.local)
                    }
                }

                addChild {
                    transformMode = ABSOLUTE
                    transform.local.translate(-4f, 8f, 2f)
                    transform.local.rotateX(2f)
                    transform.local.rotateZ(0.3f)
                    val compShape = world?.createCompound()
                    compShape?.addChild(world?.createBox(Vector3(1f))!!, Matrix4())
                    val body = world?.createBody(compShape!!, false, 1f, 0.5f, 0f, transform.local)
                    addMeshInstance()
                    addUpdate {
                        body?.transform?.get(transform.local)
                    }
                }

                addChild {
                    transformMode = ABSOLUTE
                    transform.local.translate(-4f, 4f, 2f)
                    transform.local.rotateX(2f)
                    transform.local.rotateZ(0.3f)
                    val boxShape = world?.createBox(Vector3(1f))
                    val body = world?.createBody(boxShape!!, false, 1f, 0.5f, 0f, transform.local)
                    addMeshInstance()
                    addUpdate {
                        body?.transform?.get(transform.local)
                    }
                }

                addChild {
                    transformMode = ABSOLUTE
                    transform.local.translate(0f, 4f, 4f)
                    transform.local.rotateX(0.8f)
                    transform.local.rotateZ(0.3f)
                    val boxShape = world?.createBox(Vector3(1f))
                    val body = world?.createBody(boxShape!!, false, 1f, 0.5f, 0f, transform.local)
                    addMeshInstance()
                    addUpdate {
                        body?.transform?.get(transform.local)
                    }
                }

                addChild {
                    transformMode = ABSOLUTE
                    transform.local.translate(0f, 4f, -4f)
                    transform.local.rotateX(0.8f)
                    transform.local.rotateZ(0.3f)
                    val boxShape = world?.createBox(Vector3(1f))
                    val body = world?.createBody(boxShape!!, false, 1f, 0.5f, 0f, transform.local)
                    addMeshInstance()
                    addUpdate {
                        body?.transform?.get(transform.local)
                    }
                }

                addChild {
                    transformMode = ABSOLUTE
                    transform.local.translate(0f, 8f, -4f)
                    transform.local.rotateX(0.8f)
                    transform.local.rotateZ(0.3f)
                    val boxShape = world?.createBox(Vector3(1f))
                    val body = world?.createBody(boxShape!!, false, 1f, 0.5f, 0f, transform.local)
                    addMeshInstance()
                    addUpdate {
                        body?.transform?.get(transform.local)
                    }
                }
            }
        }

        animation?.play(true)
    }

    override fun update() {
        if (KeyboardKey.KEY_D.isJustPressed(ctx.input)) {
            click?.play()
            debug = debug.not()
        }
        if (debug) {
            ctx.debug?.add("HELLO WORLD!!")

            ctx.debug?.add {
                appendln("> Audio gain = ${ctx.audio.gain}")
                appendln("> # Animations = ${animationManager.animations.size}")
                animationManager.animations.forEachIndexed { index, anim ->
                    val time = NumberFormat.getNumberInstance().format(animation?.time ?: 0)
                    appendln("> # $index [state = ${animation?.state}, timer = $time]")
                }
            }

            ctx.debug?.add {
                appendln("-".repeat(80))
                appendln("> # Rigid bodies = ${world?.bodies?.size ?: 0}")
                world?.bodies?.forEach {
                    val pos = it.transform.getTranslation(Vector3())
                    appendln("> ${pos.toString(NumberFormat.getNumberInstance())}")
                }
            }
        }

        world?.stepSimulation(1 / 60f)
        animationManager.update(1 / 60f)
        root.update()

        //println("animation(s) = ${animation.controller.time}")

        if (KeyboardKey.KEY_SPACE.isJustPressed(ctx.input)) music?.play()
        if (KeyboardKey.KEY_S.isJustPressed(ctx.input)) animation?.stop()
        if (KeyboardKey.KEY_0.isJustPressed(ctx.input)) {
            animation?.stop()
            animation?.play(false)
        }
        if (KeyboardKey.KEY_P.isJustPressed(ctx.input)) {
            if (animation?.state != AnimationState.PLAYING) animation?.play(true)
            else animation?.pause()
        }

        ctx.graphics.state {
            clearColor(0.2f, 0.2f, 0.2f, 1f)
            clearColorBuffer()
            clearDepthBuffer()
            depthTest(TestFunction.LESS)
        }

        val offX = ctx.input.mouse.x - ctx.graphics.width / 2f
        val offY = ctx.input.mouse.y - ctx.graphics.height / 2f

        val aspect = ctx.graphics.width.toFloat() / ctx.graphics.height
        val proj = Matrix4().setPerspective(java.lang.Math.toRadians(55.0).toFloat(), aspect, 0.01f, 1000f)
        val view = Matrix4().setLookAt(Vector3(6f, 4.0f + offY * 0.001f, 3f + offX * 0.001f).mul(1.25f), Vector3(0f, 1.5f, 0f), Vector3(0f, 1f, 0f))

        val stack = Stack<Actor>()

        stack.add(root)
        while (stack.isNotEmpty()) {
            val actor = stack.pop()
            actor.getComponent<JointComponent>()?.let {
                (skin[it.id] as Matrix4)
                        .set(it.actor.transform.world)
                        .mul(it.offset)
            }
            actor.children.forEach {
                stack.push(it)
            }
        }

        stack.add(root)
        while (stack.isNotEmpty()) {
            val actor = stack.pop()
            actor.getComponent<MeshInstancerComponent>()?.let { inst ->
                ctx.graphics.render(inst.mesh, staticShader) {
                    uniforms {
                        proj bindsTo "u_projection"
                        view bindsTo "u_view"
                        inst.material.diffuse bindsTo "u_texture"
                    }
                    inst.actor.children
                            .mapNotNull { it.getComponent<MeshInstanceComponent>() }
                            .forEach {
                                transform.set(it.actor.transform.world)
                                instance()
                            }
                }
            }
            actor.getComponent<SkinnedMeshInstancerComponent>()?.let { inst ->
                val mat = inst.material

                ctx.graphics.state {
                    cullMode(CullMode.FRONT_FACES)
                }

                ctx.graphics.render(inst.mesh, outlineSkinShader) {
                    uniforms {
                        skin bindsTo "u_skin"
                        proj bindsTo "u_projection"
                        view bindsTo "u_view"
                    }

                    actor.children
                            .mapNotNull { it.getComponent<SkinnedMeshInstanceComponent>() }
                            .forEach { instance() }
                }

                ctx.graphics.state {
                    cullMode(CullMode.BACK_FACES)
                }

                ctx.graphics.render(inst.mesh, skinShader) {
                    uniforms {
                        skin bindsTo "u_skin"
                        proj bindsTo "u_projection"
                        view bindsTo "u_view"
                        mat.diffuse bindsTo "u_texture"
                        mat.normals bindsTo "u_texture_normals"
                    }

                    actor.children
                            .mapNotNull { it.getComponent<SkinnedMeshInstanceComponent>() }
                            .forEach { instance() }
                }
            }

            actor.children.forEach { stack.push(it) }
        }
    }

    override fun destroy() {
        skinShader.destroy()
        outlineSkinShader.destroy()
        assetManager.destroy()
        staticShader.destroy()
        cube?.destroy()
        world?.destroy()
    }

    fun timeline(file: String): Pair<Int, MutableMap<String, AnimationTimeline>> {
        var rotKeys = mutableListOf<RotationKey>()
        var posKeys = mutableListOf<PositionKey>()
        val timelines = mutableMapOf<String, AnimationTimeline>()
        var frames = 0
        ctx.assets.loadGeneric(file)?.use {
            it.reader().forEachLine {
                if (it.startsWith("node:")) {
                    val node = it.split(":").last()
                    timelines[node] = AnimationTimeline(rotKeys, posKeys, emptyList())
                    frames = posKeys.size
                    rotKeys = mutableListOf()
                    posKeys = mutableListOf()
                } else {
                    val values = it.split("|").map { it.toFloat() }
                    rotKeys.add(RotationKey(values[0], Quaternion(values[1], values[2], values[3], values[4])))
                    posKeys.add(PositionKey(values[0], Vector3(values[5], values[6], values[7])))
                }
            }
        }
        //println(frames)
        return Pair(frames, timelines)
    }
}