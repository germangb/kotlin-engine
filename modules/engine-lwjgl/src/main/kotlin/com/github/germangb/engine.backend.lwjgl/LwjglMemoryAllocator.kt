package com.github.germangb.engine.backend.lwjgl

import com.github.germangb.engine.core.MemoryAllocator
import org.lwjgl.system.jemalloc.JEmalloc
import java.nio.ByteBuffer

/**
 * Low level memory management from by LWJGL
 */
class LwjglMemoryAllocator : MemoryAllocator {
    /**
     * Heap allocated memory
     */
    override fun malloc(size: Long) = JEmalloc.je_malloc(size)

    /**
     * Free memory
     */
    override fun free(data: ByteBuffer) = JEmalloc.je_free(data)

}