package com.github.germangb.engine.core

import java.nio.*

/**
 * Low level memory management
 */
interface BufferManager {
    /**
     * Heap allocated buffer
     */
    fun malloc(size: Long): ByteBuffer

    /**
     * Free allocated buffer
     */
    fun free(data: ByteBuffer)

    /**
     * Free allocated buffer
     */
    fun free(data: IntBuffer)

    /**
     * Free allocated buffer
     */
    fun free(data: ShortBuffer)

    /**
     * Free allocated buffer
     */
    fun free(data: LongBuffer)

    /**
     * Free allocated buffer
     */
    fun free(data: FloatBuffer)

    /**
     * Free allocated buffer
     */
    fun free(data: DoubleBuffer)
}