package com.github.germangb.engine.graphics

import com.github.germangb.engine.core.Destroyable
import java.nio.ByteBuffer

/**
 * Vertex data
 */
interface Mesh : Destroyable {
    /**
     * Vertex attributes in buffer
     */
    val attributes: Array<out VertexAttribute>

    /**
     * Mesh primitive
     */
    val primitive: MeshPrimitive

    /**
     * Number of indices to be rendered
     */
    var indices: Int

    /**
     * Set vertex data
     */
    fun setVertexData(data: ByteBuffer, offset: Long)

    /**
     * Set index data
     */
    fun setIndexData(data: ByteBuffer, offset: Long)
}
