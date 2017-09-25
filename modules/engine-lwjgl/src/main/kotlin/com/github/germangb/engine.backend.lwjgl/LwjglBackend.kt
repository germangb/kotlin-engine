package com.github.germangb.engine.backend.lwjgl

import com.github.germangb.engine.core.Backend
import com.github.germangb.engine.core.MemoryAllocator
import com.github.germangb.engine.core.ResourceLoader
import com.github.germangb.engine.graphics.Graphics

/**
 * LWJGL based backend
 */
class LwjglBackend(override val graphics: Graphics,
                   override val resources: ResourceLoader,
                   override val memory: MemoryAllocator) : Backend

