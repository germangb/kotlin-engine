package com.github.germangb.engine.plugins.debug

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.plugins.debug.DebugUtils.Companion.MODULE_NAME

/** Debug plugin methods */
interface DebugUtils {
    companion object {
        val MODULE_NAME = "debug_utils"
    }

    /** Font size */
    var fontSize: Int

    /** Add string */
    fun add(str: CharSequence)

    /** Toggle debug info */
    fun toggle()
}

/**
 * Fallback
 */
object UninstalledDebugUtils : DebugUtils {
    override var fontSize = 0
    override fun add(str: CharSequence) = Unit
    override fun toggle() = Unit
}

/**
 * Get debug plugin
 */
val Context.debug get() = getModule<DebugUtils>(MODULE_NAME) ?: UninstalledDebugUtils