package com.github.germangb.engine.backend.dektop.core

import com.github.germangb.engine.core.BufferManager
import org.lwjgl.system.jemalloc.JEmalloc.je_free
import org.lwjgl.system.jemalloc.JEmalloc.je_malloc
import java.nio.*

/**
 * Low level buffers management from by LWJGL
 */
class LWJGLBufferManager : BufferManager {
    /** Heap allocated buffers */
    override fun create(size: Long) = je_malloc(size)

    /** Free buffers */
    override fun free(data: ByteBuffer) = je_free(data)

    /** Free buffers */
    override fun free(data: IntBuffer) = je_free(data)

    /** Free buffers */
    override fun free(data: ShortBuffer) = je_free(data)

    /** Free buffers */
    override fun free(data: LongBuffer) = je_free(data)

    /** Free buffers */
    override fun free(data: FloatBuffer) = je_free(data)

    /** Free buffers*/
    override fun free(data: DoubleBuffer) = je_free(data)
}