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
    fun setText(builder: StringBuilder.() -> Unit)
}

/**
 * Get debug plugin
 */
val Context.debug get() = getPlugin(DebugPlugin::class) as? DebugUtils