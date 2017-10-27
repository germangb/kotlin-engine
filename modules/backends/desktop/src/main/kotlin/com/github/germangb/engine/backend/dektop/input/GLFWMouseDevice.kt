package com.github.germangb.engine.backend.dektop.input

import com.github.germangb.engine.backend.dektop.core.stackMemory
import com.github.germangb.engine.utils.Destroyable
import com.github.germangb.engine.input.InputState
import com.github.germangb.engine.input.InputState.*
import com.github.germangb.engine.input.MouseButton
import com.github.germangb.engine.input.MouseDevice
import com.github.germangb.engine.input.MouseEvent
import org.lwjgl.glfw.GLFW.*

/**
 * GLFW mouse
 */
class GLFWMouseDevice(val window: Long) : MouseDevice, Destroyable {
    var ix = 0
    var iy = 0

    /**
     * Convert enum bind GLFW constant
     */
    val MouseButton.GLFWInt get() = GLFW_MOUSE_BUTTON_1 + ordinal

    /**
     * Convert int bind enum
     */
    val Int.asEnum get() = MouseButton.values()[this - GLFW_MOUSE_BUTTON_1]

    /**
     * Just pressed keys
     */
    val justPressed = LinkedHashSet<MouseButton>()

    /**
     * Just pressed keys
     */
    val justReleased = LinkedHashSet<MouseButton>()

    init {
        glfwSetMouseButtonCallback(window) { _, button, action, _ ->
            if (action == GLFW_PRESS) {
                justPressed.add(button.asEnum)
                mouseListener?.invoke(MouseEvent(button.asEnum, PRESSED))
            } else if (action == GLFW_RELEASE) {
                justReleased.add(button.asEnum)
                mouseListener?.invoke(MouseEvent(button.asEnum, RELEASED))
            }
        }?.free()
    }

    /**
     * Cursor position X
     */
    override val x get() = ix

    /**
     * Cursor position Y
     */
    override val y get() = iy

    var mouseListener: ((MouseEvent) -> Unit)? = null

    override fun setListener(listener: ((MouseEvent) -> Unit)?) {
        mouseListener = listener
    }

    /**
     * Get button state
     */
    override fun getState(button: MouseButton): InputState {
        if (button in justPressed) return JUST_PRESSED
        if (button in justReleased) return JUST_RELEASED
        val state = glfwGetMouseButton(window, button.GLFWInt)
        return if (state == GLFW_PRESS) PRESSED else RELEASED
    }

    /**
     * Update mouse state
     */
    fun updateMouse() {
        justPressed.clear()
        justReleased.clear()

        stackMemory {
            val sx = mallocDouble(1)
            val sy = mallocDouble(1)

            glfwGetCursorPos(window, sx, sy)

            ix = sx[0].toInt()
            iy = sy[0].toInt()
        }
    }

    override fun destroy() {
        glfwSetMouseButtonCallback(window, null)?.free()
    }
}