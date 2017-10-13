package com.github.germangb.engine.backend.lwjgl.core

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import kotlin.reflect.KClass

/**
 * LWJGL based backend
 */
class LWJGLBackend(val runtime: LWJGLRuntime) : Context {
    val plugins = mutableMapOf<KClass<*>, Plugin>()

    override val graphics get() = runtime.gfx
    override val audio get() = runtime.audio
    override val input get() = runtime.input
    override val assets get() = runtime.res
    override val buffers get() = runtime.mem

    fun <T: Plugin> install(type: KClass<T>, plugin: T) {
        runtime.plugins.add(plugin)
        plugins.put(type, plugin)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Plugin> getPlugin(plug: KClass<T>) = plugins[plug] as T?
}

