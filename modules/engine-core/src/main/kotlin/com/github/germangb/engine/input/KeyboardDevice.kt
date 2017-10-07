package com.github.germangb.engine.input

interface KeyboardDevice {
    /**
     * Set keyboard listener
     */
    fun setListener(listener: ((state: InputState, key: KeyboardKey) -> Unit)?)

    /**
     * Get keyboard key state
     */
    fun getState(key: KeyboardKey): InputState
}