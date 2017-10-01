package com.github.germangb.engine.input

/**
 * Mouse device
 */
interface MouseDevice {
    /** Cursor X position */
    val x: Int

    /** Cursor Y position */
    val y: Int

    /**
     * Get mouse button state
     */
    fun getState(button: MouseButton): InputState
}