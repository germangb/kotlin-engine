package com.github.germangb.engine.plugins.debug

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import com.github.germangb.engine.core.getPlugin

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
    var fontSize: Int

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
    override var fontSize = 0
    private fun TODO(msg: String) = Unit
    override fun add(str: CharSequence) = TODO("DebugUtils is uninstalled")
    override fun toggle() = TODO("DebugUtils is uninstalled")
}

/**
 * Get debug plugin
 */
val Context.debug get() = (getPlugin<DebugPlugin>() as? DebugUtils) ?: UninstalledDebugUtils