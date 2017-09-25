package com.github.germangb.engine.core

import java.nio.ByteBuffer

/**
 * Low level memory management
 */
interface MemoryAllocator {
    /**
     * Heap allocated buffer
     */
    fun malloc(size: Long): ByteBuffer

    /**
     * Free allocated buffer
     */
    fun free(data: ByteBuffer)
}