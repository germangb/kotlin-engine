package com.github.germangb.engine.graphics

import java.nio.ByteBuffer

/**
 * Vertex data
 */
interface Mesh {
    /**
     * Vertex attributes in buffer
     */
    val attributes: List<VertexAttribute>

    /**
     * Mesh primitive
     */
    val primitive: MeshPrimitive

    /**
     * Set vertex data
     */
    fun setVertexData(data: ByteBuffer, offset: Long)

    /**
     * Set index data
     */
    fun setIndexData(data: ByteBuffer, offset: Long)
}
