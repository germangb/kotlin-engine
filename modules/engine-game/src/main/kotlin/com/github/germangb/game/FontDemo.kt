package com.github.germangb.game

import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Backend
import com.github.germangb.engine.framework.GameActor
import com.github.germangb.engine.framework.components.addJoint
import com.github.germangb.engine.graphics.MeshPrimitive.TRIANGLE_STRIP
import com.github.germangb.engine.graphics.TexelFormat
import com.github.germangb.engine.graphics.TextureFilter
import com.github.germangb.engine.graphics.VertexAttribute.VEC2
import com.github.germangb.engine.input.KeyboardKey
import com.github.germangb.engine.input.isJustReleased
import com.github.germangb.engine.math.Matrix4
import org.intellij.lang.annotations.Language

class FontDemo(val backend: Backend) : Application {
    val font = backend.assets.loadFont("KOMIKAX_.ttf", 32, 0..256)
    val lena = backend.assets.loadTexture("lena.bmp", TexelFormat.RGB8, TextureFilter.LINEAR, TextureFilter.LINEAR)
    val mesh = let {
        val vert = backend.buffers.malloc(2 * 4 * 4)
        val index = backend.buffers.malloc(4 * 4)
        vert.putFloat(0f).putFloat(0f)
        vert.putFloat(1f).putFloat(0f)
        vert.putFloat(0f).putFloat(1f)
        vert.putFloat(1f).putFloat(1f).flip()
        index.putInt(0).putInt(1).putInt(2).putInt(3).flip()
        val mesh = backend.graphics.createMesh(vert, index, TRIANGLE_STRIP, listOf(VEC2))
        vert.clear()
        index.clear()
        backend.buffers.free(vert)
        backend.buffers.free(index)
        mesh
    }
    val shader = let {
        @Language("GLSL")
        val vert = """#version 450 core
            layout(location = 0) in vec2 a_position;
            out vec2 v_uv;
            void main () {
                gl_Position = vec4((a_position*2-1) / vec2(4./3, 1), 0.0, 1.0);
                v_uv = vec2(a_position.x, 1-a_position.y);
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
    val root = GameActor()

    override fun init() {
        root.send("ping") {
            println("(hopefully pong...) -> $it")
        }

        root.addChild {
            addChild {
                //...
                addJoint(1, Matrix4())
            }
            addChild {
                //...
            }
        }
    }

    override fun update() {
        root.update()

        if (KeyboardKey.KEY_T.isJustReleased(backend.input)) {
            toggle = !toggle
        }

        backend.graphics.state {
            clearColor(0.2f, 0.2f, 0.2f, 1f)
            clearColorBuffer()
        }

        backend.graphics.render(mesh, shader) {
            uniforms {
                if (toggle) {
                    font?.texture?.let {
                        it bindsTo "u_texture"
                    }
                } else {
                    lena?.let {
                        it bindsTo "u_texture"
                    }
                }
            }
            instance()
        }
    }

    override fun destroy() {
        font?.destroy()
        lena?.destroy()
        mesh.destroy()
        shader.destroy()
    }

}