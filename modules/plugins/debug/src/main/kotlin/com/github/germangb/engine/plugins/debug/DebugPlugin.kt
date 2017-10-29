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
     * Set debug text
     */
    fun add(build: StringBuilder.() -> Unit)

    /**
     * Add string
     */
    fun add(str: CharSequence)
}

/**
 * Fallback
 */
object UninstalledDebugUtils : DebugUtils {
    private fun TODO(msg: String) = Unit
    override fun add(build: StringBuilder.() -> Unit) = TODO("DebugUtils is uninstalled")
    override fun add(str: CharSequence) = TODO("DebugUtils is uninstalled")
}

/**
 * Get debug plugin
 */
val Context.debug get() = (getPlugin(DebugPlugin::class) as? DebugUtils) ?: UninstalledDebugUtils