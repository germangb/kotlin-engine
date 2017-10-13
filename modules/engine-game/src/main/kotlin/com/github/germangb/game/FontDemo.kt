package com.github.germangb.game

import com.github.germangb.engine.animation.*
import com.github.germangb.engine.assets.NaiveAssetManager
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component
import com.github.germangb.engine.framework.Material
import com.github.germangb.engine.framework.Materialc
import com.github.germangb.engine.framework.components.*
import com.github.germangb.engine.graphics.CullMode
import com.github.germangb.engine.graphics.TestFunction
import com.github.germangb.engine.graphics.TexelFormat.RGB8
import com.github.germangb.engine.graphics.TextureFilter.NEAREST
import com.github.germangb.engine.graphics.VertexAttribute.*
import com.github.germangb.engine.input.InputState
import com.github.germangb.engine.input.KeyboardKey
import com.github.germangb.engine.input.isJustPressed
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Quaternion
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.plugin.physics.physics
import org.intellij.lang.annotations.Language
import java.util.*

val Materialc.diffuse get() = getTexture("diffuse")
val Materialc.normals get() = getTexture("normals")

class FontDemo(val backend: Context) : Application {
    val animationManager = SimpleAnimationManager()
    val assetManager = NaiveAssetManager(backend.assets)
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
        backend.graphics.createShaderProgram(vert, frag)
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

                gl_Position = u_projection * u_view * a_instance * u_skin_transform * vec4(a_position, 1.0);
                v_normal = normalize((a_instance * u_skin_transform * vec4(a_normal, 0.0)).xyz);
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
        backend.graphics.createShaderProgram(vert, frag)
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
        backend.graphics.createShaderProgram(vert, frag)
    }
    var toggle = true
    val root = Actor()
    val animation by lazy {
        animationManager.createAnimation(ActorAnimationController(root, 119f, 24, timeline("idle2.txt"), interpolate = true))
    }
    val cube = backend.assets.loadMesh("cube.blend", setOf(POSITION, NORMAL, UV))
    val world by lazy { backend.physics?.createWorld(Vector3(0f, -9.8f, 0f)) }

    override fun init() {
        world?.createBox(0f, 0f, 0.5f, Vector3(16f, 0.1f, 16f), Matrix4())

        backend.input.keyboard.setListener { (key, state) ->
            if (state == InputState.PRESSED && key.isPrintable)
                println("$key")
        }

        backend.input.mouse.setListener { (button, state) ->
            println("$button $state")
        }

        backend.assets.loadActor("hellknight.md5mesh", assetManager)?.let {
            root.addChild {
                it.invoke(this)
                transform.local.scale(0.025f)
            }
        }

        assetManager.loadTexture("cube.png", RGB8, NEAREST, NEAREST)

        cube?.let { mesh ->
            assetManager.getTexture("cube.png")?.let { tex ->
                root.addChild {
                    val mat = Material()
                    mat.setTexture("diffuse", tex)
                    addMeshInstancer(mesh, mat)
                    addChild {
                        transform.local.translate(0f, 4f, -4f)
                        transform.local.rotateX(0.8f)
                        transform.local.rotateZ(0.3f)
                        addMeshInstance()

                        val body = world?.createBox(1f, 1f, 0.5f, Vector3(0.5f),transform.local)

                        addUpdate {
                            body?.transform?.get(transform.local)
                        }
                    }
                }
            }
        }

        animation.stop()
        animation.play()
        //animation.controller.seek(24*8f)
    }

    override fun update() {
        world?.step(1/60f)
        animationManager.update(1 / 60f)
        root.update()

        if (KeyboardKey.KEY_P.isJustPressed(backend.input)) {
            toggle = !toggle
            if (toggle) animation.play()
            else animation.pause()
        }

        backend.graphics.state {
            clearColor(0.2f, 0.2f, 0.2f, 1f)
            clearColorBuffer()
            clearDepthBuffer()
            depthTest(TestFunction.LESS)
        }

        val aspect = backend.graphics.width.toFloat() / backend.graphics.height
        val proj = Matrix4().setPerspective(java.lang.Math.toRadians(55.0).toFloat(), aspect, 0.01f, 1000f)
        val view = Matrix4()
                .setLookAt(Vector3(3f, 1.0f, 1f), Vector3(0f, 1.5f, 0f), Vector3(0f, 1f, 0f))
                .setLookAt(Vector3(6f, 4.0f, 3f), Vector3(0f, 1.5f, 0f), Vector3(0f, 1f, 0f))

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
                backend.graphics.render(inst.mesh, staticShader) {
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

                backend.graphics.state {
                    cullMode(CullMode.FRONT_FACES)
                }

                backend.graphics.render(inst.mesh, outlineSkinShader) {
                    uniforms {
                        skin bindsTo "u_skin"
                        proj bindsTo "u_projection"
                        view bindsTo "u_view"
                    }

                    actor.children
                            .mapNotNull { it.getComponent<SkinnedMeshInstanceComponent>() }
                            .forEach { instance() }
                }

                backend.graphics.state {
                    cullMode(CullMode.BACK_FACES)
                }

                backend.graphics.render(inst.mesh, skinShader) {
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

    fun timeline(file: String): MutableMap<String, AnimationTimeline> {
        var rotKeys = mutableListOf<RotationKey>()
        var posKeys = mutableListOf<PositionKey>()
        val timelines = mutableMapOf<String, AnimationTimeline>()
        backend.assets.loadGeneric(file)?.use {
            it.reader().forEachLine {
                if (it.startsWith("node:")) {
                    val node = it.split(":").last()
                    timelines[node] = AnimationTimeline(rotKeys, posKeys, emptyList())
                    rotKeys = mutableListOf()
                    posKeys = mutableListOf()
                } else {
                    val values = it.split("|").map { it.toFloat() }
                    rotKeys.add(RotationKey(values[0], Quaternion(values[1], values[2], values[3], values[4])))
                    posKeys.add(PositionKey(values[0], Vector3(values[5], values[6], values[7])))
                }
            }
        }
        return timelines
    }
}