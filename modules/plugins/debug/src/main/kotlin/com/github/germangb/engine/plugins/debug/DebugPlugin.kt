package com.github.germangb.engine.plugins.debug

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin

/**
 * Plugin interface
 */
interface DebugPlugin : Plugin, DebugAPI

/**
 * Debug plugin methods
 */
interface DebugAPI {
    /**
     * Add debug addString
     */
    fun addString(x: Float, y: Float, text: String)

    /**
     * Adds a string to the list
     */
    fun addString(text: String)
}

/**
 * Get debug plugin
 */
val Context.debug get() = getPlugin(DebugPlugin::class) as? DebugAPI