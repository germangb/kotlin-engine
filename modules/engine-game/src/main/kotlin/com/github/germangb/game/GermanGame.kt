package com.github.germangb.game

import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Backend
import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.MeshPrimitive
import com.github.germangb.engine.graphics.ShaderProgram
import com.github.germangb.engine.graphics.VertexAttribute

class GermanGame(val backend: Backend) : Application {

    lateinit var triangle: Mesh
    lateinit var shader: ShaderProgram

    override fun init() {
        val vertex = backend.memory.malloc(1000)
        with(vertex) {
            putFloat(0f); putFloat(0f); putFloat(0f)
            putFloat(1f); putFloat(0f); putFloat(0f)
            putFloat(1f); putFloat(1f); putFloat(0f)
            flip()
        }

        val index = backend.memory.malloc(1000)
        with(index) {
            putInt(0)
            putInt(1)
            putInt(2)
            flip()
        }

        triangle = backend.graphics.createMesh(vertex, index, MeshPrimitive.TRIANGLES, listOf(VertexAttribute.POSITION))

        //Language("GLSL")
        val vert = """#version 450
layout(location = 0) in vec3 a_position;
void main() {
    gl_Position = vec4(a_position, 1.0);
}"""
        //Language("GLSL")
        val frag = """#version 450
out vec4 frag_color;
void main() {
    frag_color = vec4(1.0);
}"""
        shader = backend.graphics.createShaderProgram(vert, frag, listOf(VertexAttribute.POSITION))
    }

    override fun update() {
        backend.graphics.state {
            clearColor(0.2f, 0.2f, 0.2f, 1f)
            clearColorBuffer()
        }

        backend.graphics.render(triangle, shader) {
            instance()
        }
    }
}