package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.graphics.InstanceAttribute
import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.MeshPrimitive
import com.github.germangb.engine.graphics.VertexAttribute
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL30.glDeleteVertexArrays
import java.nio.ByteBuffer

/**
 * OpenGL mesh
 */
class GLMesh(val gfx: GLGraphicsDevice,
             val vbo: Int,
             val ibo: Int,
             val vao: Int,
             val indexType: Int,
             override var indices: Int,
             override val primitive: MeshPrimitive,
             override val attributes: Array<out VertexAttribute>,
             override val instanceAttributes: Array<out InstanceAttribute>) : Mesh {
    init {
        gfx.imeshes.add(this)
    }

    override fun destroy() {
        glDeleteBuffers(vbo)
        glDeleteBuffers(ibo)
        glDeleteVertexArrays(vao)
        gfx.imeshes.remove(this)
    }

    /**
     * Buffer stride
     */
    val stride = let {
        var count = 0
        attributes.forEach { count += it.size * it.type.bytes }
        count
    }

    /** Instance buffer stride */
    val instanceStride = let {
        var count = 0
        instanceAttributes.forEach { count += it.size * it.type.bytes }
        count
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
        indices = data.capacity() / 4
    }
}