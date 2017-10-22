package com.github.germangb.engine.input

/**
 * Input event
 */
data class KeyboardEvent(val key: KeyboardKey, val state: InputState)

/**
 * Listener alias
 */
typealias KeyboardListener = (KeyboardEvent) -> Unit

/**
 * Keyboard device
 */
interface KeyboardDevice {
    /**
     * Set keyboard listener
     */
    fun setListener(listener: ((KeyboardEvent) -> Unit)?)

    /**
     * Get keyboard key state
     */
    fun getState(key: KeyboardKey): InputState
}