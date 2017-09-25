package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.backend.lwjgl.glCheckError
import com.github.germangb.engine.graphics.Instancer
import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.ShaderProgram
import com.github.germangb.engine.graphics.Uniforms
import com.github.germangb.engine.math.Matrix4
import org.lwjgl.system.jemalloc.JEmalloc
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL31.*
import org.lwjgl.opengl.GL33.*

class LwjglInstancer : Instancer {
    override val transform = Matrix4()

    /** Buffer size */
    private val data = JEmalloc.je_malloc(64*4*1024)
    private val buffer = glGenBuffers()

    init {
        glCheckError("Error in LwjglInstancer.init while creating vertex buffer") {
            glBindBuffer(GL_ARRAY_BUFFER, buffer)
            glBufferData(GL_ARRAY_BUFFER, data, GL_STREAM_DRAW)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
        }
    }

    /**
     * Begin draw call
     */
    fun begin(mesh: LwjglMesh, program: LwjglShaderProgram) {
        glCheckError("Error in LwjglInstancer.begin()") {
            glUseProgram(program.program)
            glBindVertexArray(mesh.vao)
            glBindBuffer(GL_ARRAY_BUFFER, mesh.vbo)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.ibo)
        }
    }

    fun flush() {
        glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0L)
    }

    fun end() {
        flush()
        glCheckError("Error in LwjglInstancer.end()") {
            glUseProgram(0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        }
    }

    fun free() {
        glCheckError("Error in LwjglInstancer.free() while deleting vertex buffer") {
            data.clear()
            JEmalloc.je_free(data)
            glDeleteBuffers(buffer)
        }
    }

    override fun instance() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun uniforms(action: Uniforms.() -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}