package com.github.germangb.engine.backend.dektop.core

import com.github.germangb.engine.core.Context

/**
 * LWJGL based ctx
 */
class LWJGLContext(val runtime: LWJGLRuntime) : Context {
    /** Installed plugins */
    private val plugins = mutableMapOf<String, DesktopModule>()

    override val graphics get() = runtime.gfx
    override val audio get() = runtime.audio
    override val input get() = runtime.input
    override val buffers get() = runtime.mem
    override val files get() = runtime.files
    override val time get() = runtime.time

    /**
     * Install plugin in backend
     */
    fun <T : DesktopModule> installModule(name: String, plugin: T) {
        runtime.plugins.add(plugin)
        plugins.put(name, plugin)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getModule(name: String) = plugins[name] as? T?
}

