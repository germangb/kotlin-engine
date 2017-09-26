package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.MeshPrimitive
import com.github.germangb.engine.graphics.VertexAttribute
import java.nio.ByteBuffer
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL30.*

/**
 * OpenGL mesh
 */
class LwjglMesh(val vbo: Int,
                val ibo: Int,
                val vao: Int,
                override var indices: Int,
                override val primitive: MeshPrimitive,
                override val attributes: List<VertexAttribute>) : Mesh {
    override fun destroy() {
        glDeleteBuffers(vbo)
        glDeleteBuffers(ibo)
        glDeleteVertexArrays(vao)
    }

    /**
     * Buffer stride
     */
    val stride: Int

    init {
        var istride = 0
        attributes.forEach { istride += it.size }
        stride = istride
    }

    /**
     * Update a portion of the vertex buffer
     */
    override fun setVertexData(data: ByteBuffer, offset: Long) {
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferSubData(GL_ARRAY_BUFFER, offset, data)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    /**
     * Update a portion of the index buffer
     */
    override fun setIndexData(data: ByteBuffer, offset: Long) {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, offset, data)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        indices = data.capacity()/4
    }

}