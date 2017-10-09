package com.github.germangb.game

import com.github.germangb.engine.animation.ActorAnimation
import com.github.germangb.engine.animation.AnimationTimeline
import com.github.germangb.engine.animation.PositionKey
import com.github.germangb.engine.animation.RotationKey
import com.github.germangb.engine.assets.NaiveAssetManager
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Backend
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.components.JointComponent
import com.github.germangb.engine.framework.components.SkinnedMeshInstanceComponent
import com.github.germangb.engine.framework.components.SkinnedMeshInstancerComponent
import com.github.germangb.engine.graphics.TestFunction
import com.github.germangb.engine.input.InputState
import com.github.germangb.engine.input.KeyboardKey
import com.github.germangb.engine.input.isJustReleased
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Quaternion
import com.github.germangb.engine.math.Vector3
import org.intellij.lang.annotations.Language
import java.util.*

class FontDemo(val backend: Backend) : Application {
    val manager = NaiveAssetManager(backend.assets)
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
            uniform mat4 u_projection;
            uniform mat4 u_skin[110];
            void main () {
                mat4 u_skin_transform = u_skin[int(a_bones.x+0.001)] * a_weights.x +
                                        u_skin[int(a_bones.y+0.001)] * a_weights.y +
                                        u_skin[int(a_bones.z+0.001)] * a_weights.z +
                                        u_skin[int(a_bones.w+0.001)] * a_weights.w;
//                mat4 u_skin_transform = u_skin[0] * a_weights.x +
//                                        u_skin[0] * a_weights.y +
//                                        u_skin[0] * a_weights.z +
//                                        u_skin[0] * a_weights.w;
                gl_Position = u_projection * a_instance * u_skin_transform * vec4(a_position, 1.0);
                v_uv = a_uv;
            }
        """.trimMargin()
        @Language("GLSL")
        val frag = """#version 450 core
            in vec2 v_uv;
            out vec4 frag_color;
            uniform sampler2D u_texture;
            void main() {
                frag_color = vec4(texture(u_texture, v_uv).rgb, 1.0);
            }
        """.trimMargin()
        backend.graphics.createShaderProgram(vert, frag)
    }
    var toggle = false
    val root = Actor()

    val animation = let {
        backend.assets.loadActor("hellknight.md5mesh", manager)?.let {
            root.addChild(it)
        }

        backend.assets.loadGeneric("animation.txt")?.use {
            var rotKeys = mutableListOf<RotationKey>()
            var posKeys = mutableListOf<PositionKey>()
            val timelines = mutableMapOf<String, AnimationTimeline>()
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
            ActorAnimation(root, 119f, timelines)
        }
    }

    override fun init() {
        root.transform.local.scale(0.025f)
        root.send("ping") {
            println("(hopefully pong...) -> $it")
        }

        backend.input.keyboard.setListener { (key, state) ->
            if (state == InputState.PRESSED && key.isPrintable)
                println("$key")
        }

        backend.input.mouse.setListener { (button, state) ->
            println("$button $state")
        }

        //manager.dump()
    }

    override fun update() {
        animation?.update(1 / 60f)
        root.update()

        if (KeyboardKey.KEY_T.isJustReleased(backend.input)) {
            toggle = !toggle
        }

        backend.graphics.state {
            clearColor(0.2f, 0.2f, 0.2f, 1f)
            clearColorBuffer()
            clearDepthBuffer()
            depthTest(TestFunction.LESS)
        }

        val proj = Matrix4()
                .setPerspective(java.lang.Math.toRadians(55.0).toFloat(), 4 / 3f, 0.01f, 1000f)
                .lookAt(Vector3(6f, 4f, 3f), Vector3(0f, 1.4f, 0f), Vector3(0f, 1f, 0f))


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
        manager.destroy()
    }

}