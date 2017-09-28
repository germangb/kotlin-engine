package com.github.germangb.engine.backend.lwjgl.input

import org.lwjgl.glfw.GLFW.*
import com.github.germangb.engine.backend.lwjgl.core.stackMemory
import com.github.germangb.engine.input.MouseDevice

/**
 * GLFW mouse
 */
class GLFWMouse(val window: Long) : MouseDevice {
    private var ix = 0
    private var iy = 0

    override val x get() = ix
    override val y get() = iy

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
}