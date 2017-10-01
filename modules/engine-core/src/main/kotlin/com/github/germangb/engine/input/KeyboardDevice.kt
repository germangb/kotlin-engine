package com.github.germangb.engine.input

interface KeyboardDevice {
    /**
     * Get keyboard key state
     */
    fun getState(key: KeyboardKey): InputState
}