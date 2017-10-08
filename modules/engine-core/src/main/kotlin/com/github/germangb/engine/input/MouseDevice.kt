package com.github.germangb.engine.input

/** Input event */
data class MouseInputEvent(val button: MouseButton, val state: InputState)

/**
 * Listener alias
 */
typealias MouseInputListener = (MouseInputEvent) -> Unit

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
    fun setListener(listener: ((MouseInputEvent) -> Unit)?)

    /**
     * Get mouse button state
     */
    fun getState(button: MouseButton): InputState
}