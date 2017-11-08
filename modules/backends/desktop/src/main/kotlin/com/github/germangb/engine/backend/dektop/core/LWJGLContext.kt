package com.github.germangb.engine.backend.dektop.core

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import com.github.germangb.engine.core.Time
import kotlin.reflect.KClass

/**
 * LWJGL based ctx
 */
class LWJGLContext(val runtime: LWJGLRuntime) : Context {
    /** Installed plugins */
    val plugins = mutableMapOf<KClass<*>, Plugin>()

    override val graphics get() = runtime.gfx
    override val audio get() = runtime.audio
    override val input get() = runtime.input
    override val buffers get() = runtime.mem
    override val files get() = runtime.files
    override val time get() = runtime.time

    /**
     * Install plugin in backend
     */
    fun <T : Plugin> install(type: KClass<T>, plugin: T) {
        runtime.plugins.add(plugin)
        plugins.put(type, plugin)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Plugin> getPlugin(plug: KClass<T>) = plugins[plug] as T?
}

