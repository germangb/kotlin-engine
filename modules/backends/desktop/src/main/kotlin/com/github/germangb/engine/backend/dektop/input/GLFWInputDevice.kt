package com.github.germangb.engine.backend.dektop.input

import com.github.germangb.engine.utils.Destroyable
import com.github.germangb.engine.input.InputDevice

/**
 * GLFW input
 */
class GLFWInputDevice(window: Long, width: Int, height: Int) : InputDevice, Destroyable {
    /**
     * GLFW mouse
     */
    override val mouse = GLFWMouseDevice(window, width, height)

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