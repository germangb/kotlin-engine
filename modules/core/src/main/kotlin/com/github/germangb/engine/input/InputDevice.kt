package com.github.germangb.engine.input

/**
 * Input methods
 */
interface InputDevice {
    /**
     * Get mouse device
     */
    val mouse: MouseDevice

    /**
     * Get keyboard device
     */
    val keyboard: KeyboardDevice
}