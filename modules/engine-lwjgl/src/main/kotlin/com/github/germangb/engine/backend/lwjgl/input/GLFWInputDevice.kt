package com.github.germangb.engine.backend.lwjgl.input

import com.github.germangb.engine.core.Destroyable
import com.github.germangb.engine.input.InputDevice

/**
 * GLFW input
 */
class GLFWInputDevice(val window: Long) : InputDevice, Destroyable {
    /**
     * GLFW-based mouse
     */
    override val mouse = GLFWMouse(window)

    /** Update input */
    fun updateInput() {
        mouse.updateMouse()
    }

    override fun destroy() {
        //(mouse as GLFWMouse).destroy()
    }
}