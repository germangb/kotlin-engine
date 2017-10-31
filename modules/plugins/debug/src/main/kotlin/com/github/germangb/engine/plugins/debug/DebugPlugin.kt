package com.github.germangb.engine.plugins.debug

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin

/**
 * Plugin interface
 */
interface DebugPlugin : Plugin, DebugUtils

/**
 * Debug plugin methods
 */
interface DebugUtils {
    /**
     * Font size
     */
    var fontSize: Float

    /**
     * Set debug text
     */
    fun add(build: StringBuilder.() -> Unit)

    /**
     * Add string
     */
    fun add(str: CharSequence)

    /**
     * Toggle debug info
     */
    fun toggle()
}

/**
 * Fallback
 */
object UninstalledDebugUtils : DebugUtils {
    override var fontSize: Float
        get() = 0f
        set(value) = Unit
    private fun TODO(msg: String) = Unit
    override fun add(build: StringBuilder.() -> Unit) = TODO("DebugUtils is uninstalled")
    override fun add(str: CharSequence) = TODO("DebugUtils is uninstalled")
    override fun toggle() = TODO("DebugUtils is uninstalled")
}

/**
 * Get debug plugin
 */
val Context.debug get() = (getPlugin(DebugPlugin::class) as? DebugUtils) ?: UninstalledDebugUtils