package com.github.germangb.engine.backend.lwjgl.input

import com.github.germangb.engine.backend.lwjgl.core.stackMemory
import com.github.germangb.engine.core.Destroyable
import com.github.germangb.engine.input.InputState
import com.github.germangb.engine.input.InputState.PRESSED
import com.github.germangb.engine.input.InputState.RELEASED
import com.github.germangb.engine.input.MouseButton
import com.github.germangb.engine.input.MouseDevice
import org.lwjgl.glfw.GLFW.*

/**
 * GLFW mouse
 */
class GLFWMouseDevice(val window: Long) : MouseDevice, Destroyable {
    var ix = 0
    var iy = 0

    /**
     * Convert enum to GLFW constant
     */
    val MouseButton.GLFWInt get() = GLFW_MOUSE_BUTTON_1 + ordinal

    /**
     * Cursor position X
     */
    override val x get() = ix

    /**
     * Cursor position Y
     */
    override val y get() = iy

    /**
     * Get button state
     */
    override fun getState(button: MouseButton): InputState {
        val state = glfwGetMouseButton(window, button.GLFWInt)
        return if (state == GLFW_PRESS) PRESSED else RELEASED
    }

    /**
     * Update mouse state
     */
    fun updateMouse() {
        stackMemory {
            val sx = mallocDouble(1)
            val sy = mallocDouble(1)

            glfwGetCursorPos(window, sx, sy)

            ix = sx[0].toInt()
            iy = sy[0].toInt()
        }
    }

    override fun destroy() = Unit
}