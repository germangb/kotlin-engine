package com.github.germangb.engine.backend.lwjgl.core

import com.github.germangb.engine.core.Backend

/**
 * LWJGL based backend
 */
class LWJGLBackend(val runtime: LWJGLRuntime) : Backend {
    override val graphics get() = runtime.gfx
    override val audio get() = runtime.audio
    override val input get() = runtime.input
    override val assets get() = runtime.res
    override val buffers get() = runtime.mem
}
