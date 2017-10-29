package com.github.germangb.engine.graphics

import com.github.germangb.engine.utils.Destroyable
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
     * Instance attributes
     */
    val instanceAttributes: Array<out InstanceAttribute>

    /**
     * Mesh primitive
     */
    val primitive: MeshPrimitive

    /**
     * Number of indices bind be rendered
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
