package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.MeshPrimitive
import com.github.germangb.engine.graphics.VertexAttribute
import java.nio.ByteBuffer

/**
 * OpenGL mesh
 */
class LwjglMesh(val vbo: Int,
                val ibo: Int,
                val vao: Int,
                val indices: Int,
                override val primitive: MeshPrimitive,
                override val attributes: List<VertexAttribute>) : Mesh {

    /**
     * Attribute offset
     */
    val offset: Map<VertexAttribute, Int> = mutableMapOf()

    /**
     * Buffer stride
     */
    val stride: Int

    init {
        var istride = 0
        var ioffset = 0

        // compute stride & attribute offset
        attributes.forEach {
            (offset as MutableMap)[it] = ioffset
            istride += it.size
            ioffset += it.size
        }
        stride = istride
    }

    /**
     * Update a portion of the vertex buffer
     */
    override fun setVertexData(data: ByteBuffer, offset: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Update a portion of the index buffer
     */
    override fun setIndexData(data: ByteBuffer, offset: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}