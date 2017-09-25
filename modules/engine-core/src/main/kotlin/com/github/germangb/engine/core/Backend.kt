package com.github.germangb.engine.core

import com.github.germangb.engine.graphics.Graphics

/**
 * Engine backend
 */
interface Backend {
    /**
     * Graphics backend
     */
    val graphics: Graphics

    /**
     * Resources backend
     */
    val resources: ResourceLoader

    /**
     * Memory allocator
     */
    val memory: MemoryAllocator
}
