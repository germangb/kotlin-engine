package com.github.germangb.engine.backend.lwjgl.input

import com.github.germangb.engine.utils.Destroyable
import com.github.germangb.engine.input.InputDevice

/**
 * GLFW input
 */
class GLFWInputDevice(window: Long) : InputDevice, Destroyable {
    /**
     * GLFW mouse
     */
    override val mouse = GLFWMouseDevice(window)

    /**
     * GLFW keyboard
     */
    override val keyboard = GLFWKeyboardDevice(window)

    /** Update input */
    fun updateInput() {
        mouse.updateMouse()
        keyboard.updateKeyboard()
    }

    override fun destroy() {
        mouse.destroy()
        keyboard.destroy()
    }
}