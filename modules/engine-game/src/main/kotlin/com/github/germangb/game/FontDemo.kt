package com.github.germangb.game

import com.github.germangb.engine.animation.*
import com.github.germangb.engine.assets.NaiveAssetManager
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Backend
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.components.JointComponent
import com.github.germangb.engine.framework.components.SkinnedMeshInstanceComponent
import com.github.germangb.engine.framework.components.SkinnedMeshInstancerComponent
import com.github.germangb.engine.graphics.TestFunction
import com.github.germangb.engine.input.*
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Quaternion
import com.github.germangb.engine.math.Vector3
import org.intellij.lang.annotations.Language
import java.util.*

class FontDemo(val backend: Backend) : Application {
    val animationManager = SimpleAnimationManager()
    val assetManager = NaiveAssetManager(backend.assets)
    val skin = Array<Matrix4c>(110) { Matrix4() }
    val shader = let {
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
            uniform mat4 u_skin[110];
            void main () {
                mat4 u_skin_transform = u_skin[int(a_bones.x+0.001)] * a_weights.x +
                                        u_skin[int(a_bones.y+0.001)] * a_weights.y +
                                        u_skin[int(a_bones.z+0.001)] * a_weights.z +
                                        u_skin[int(a_bones.w+0.001)] * a_weights.w;
                gl_Position = u_projection * a_instance * u_skin_transform * vec4(a_position, 1.0);
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
                float light = clamp(dot(v_normal, vec3(1, 0, 0)), 0.0, 1.0);
                frag_color = vec4(texture(u_texture, v_uv).rgb*smoothstep(0, 1, light), 1.0);
            }
        """.trimMargin()
        backend.graphics.createShaderProgram(vert, frag)
    }
    var toggle = false
    val root = Actor()
    val animation by lazy {
        animationManager.createAnimation(ActorAnimationController(root, 119f, 24, timeline("idle2.txt"), interpolate = true))
    }

    var t = 0f

    override fun init() {
        root.transform.local.scale(0.025f)

        backend.input.keyboard.setListener { (key, state) ->
            if (state == InputState.PRESSED && key.isPrintable)
                println("$key")
        }

        backend.input.mouse.setListener { (button, state) ->
            println("$button $state")
        }

        backend.assets.loadActor("hellknight.md5mesh", assetManager)?.let {
            root.addChild(it)
        }

        animation.stop()
        //animation.play()
    }

    override fun update() {
        animationManager.update(1 / 60f)
        root.update()

        if (KeyboardKey.KEY_P.isJustPressed(backend.input)) {
            toggle = !toggle
            if (toggle) animation.play()
            else animation.pause()
        }

        if (animation.state != AnimationState.PLAYING) {
            if (KeyboardKey.KEY_LEFT.isPressed(backend.input)) t -= 1 / 60f
            if (KeyboardKey.KEY_RIGHT.isPressed(backend.input)) t += 1 / 60f
            (animation as? ManagedAnimation)?.controller?.seek(t)
        }

        backend.graphics.state {
            clearColor(0.2f, 0.2f, 0.2f, 1f)
            clearColorBuffer()
            clearDepthBuffer()
            depthTest(TestFunction.LESS)
        }

        val proj = Matrix4()
                .setPerspective(java.lang.Math.toRadians(55.0).toFloat(), 4 / 3f, 0.01f, 1000f)
                .lookAt(Vector3(3f, 1.0f, 1f), Vector3(0f, 1.5f, 0f), Vector3(0f, 1f, 0f))


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
            actor.getComponent<SkinnedMeshInstancerComponent>()?.let { inst ->
                inst.mesh.resource?.let { mesh ->
                    backend.graphics.render(mesh, shader) {
                        uniforms {
                            skin bindsTo "u_skin"
                            proj bindsTo "u_projection"
                            inst.texture.resource?.let { it bindsTo "u_texture" }
                        }

                        actor.children
                                .mapNotNull { it.getComponent<SkinnedMeshInstanceComponent>() }
                                .forEach {
                                    instance()
                                }
                    }
                }
            }

            actor.children.forEach { stack.push(it) }
        }
    }

    override fun destroy() {
        shader.destroy()
        assetManager.destroy()
    }

    fun timeline(file: String): MutableMap<String, AnimationTimeline> {
        var rotKeys = mutableListOf<RotationKey>()
        var posKeys = mutableListOf<PositionKey>()
        val timelines = mutableMapOf<String, AnimationTimeline>()
        backend.assets.loadGeneric(file)?.use {
            it.reader().forEachLine {
                if (it.startsWith("node:")) {
                    val node = it.split(":").last()
                    timelines[node] = AnimationTimeline(rotKeys, posKeys)
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