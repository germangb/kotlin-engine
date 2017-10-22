package com.github.germangb.engine.input

/**
 * Input event
 */
data class MouseEvent(val button: MouseButton, val state: InputState)

/**
 * Listener alias
 */
typealias MouseListener = (MouseEvent) -> Unit

/**
 * Mouse device
 */
interface MouseDevice {
    /** Cursor X position */
    val x: Int

    /** Cursor Y position */
    val y: Int

    /**
     * Set keyboard listener
     */
    fun setListener(listener: ((MouseEvent) -> Unit)?)

    /**
     * Get mouse button state
     */
    fun getState(button: MouseButton): InputState
}