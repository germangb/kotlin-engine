package com.github.germangb.engine.backend.lwjgl.core

import com.github.germangb.engine.core.BufferManager
import org.lwjgl.system.jemalloc.JEmalloc
import java.nio.*

/**
 * Low level buffers management from by LWJGL
 */
class LWJGLBufferManager : BufferManager {
    /**
     * Heap allocated buffers
     */
    override fun malloc(size: Long) = JEmalloc.je_malloc(size)

    /**
     * Free buffers
     */
    override fun free(data: ByteBuffer) = JEmalloc.je_free(data)

    /**
     * Free buffers
     */
    override fun free(data: IntBuffer) = JEmalloc.je_free(data)

    /**
     * Free buffers
     */
    override fun free(data: ShortBuffer) = JEmalloc.je_free(data)

    /**
     * Free buffers
     */
    override fun free(data: LongBuffer) = JEmalloc.je_free(data)

    /**
     * Free buffers
     */
    override fun free(data: FloatBuffer) = JEmalloc.je_free(data)

    /**
     * Free buffers
     */
    override fun free(data: DoubleBuffer) = JEmalloc.je_free(data)
}