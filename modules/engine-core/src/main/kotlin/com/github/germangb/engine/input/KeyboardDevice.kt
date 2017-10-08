package com.github.germangb.engine.input

/** Input event */
data class KeyboardInputEvent(val key: KeyboardKey, val state: InputState)

/**
 * Listener alias
 */
typealias KeyboardInputListener = (KeyboardInputEvent) -> Unit

interface KeyboardDevice {
    /**
     * Set keyboard listener
     */
    fun setListener(listener: ((KeyboardInputEvent) -> Unit)?)

    /**
     * Get keyboard key state
     */
    fun getState(key: KeyboardKey): InputState
}