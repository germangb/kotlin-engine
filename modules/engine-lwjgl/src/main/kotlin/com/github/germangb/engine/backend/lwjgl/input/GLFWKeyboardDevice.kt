package com.github.germangb.engine.backend.lwjgl.input

import com.github.germangb.engine.core.Destroyable
import com.github.germangb.engine.input.InputState
import com.github.germangb.engine.input.InputState.*
import com.github.germangb.engine.input.KeyboardDevice
import com.github.germangb.engine.input.KeyboardKey
import org.lwjgl.glfw.GLFW.*
import kotlin.collections.LinkedHashSet

class GLFWKeyboardDevice(val window: Long) : KeyboardDevice, Destroyable {
    /**
     * Just pressed keys
     */
    val justPressed = LinkedHashSet<KeyboardKey>()

    /**
     * Just pressed keys
     */
    val justReleased = LinkedHashSet<KeyboardKey>()

    init {
        glfwSetKeyCallback(window) { _, key, _, action, _ ->
            if (action == GLFW_PRESS) justPressed.add(key.asEnum)
            else if (action == GLFW_RELEASE) justReleased.add(key.asEnum)
        }?.free()
    }

    fun updateKeyboard() {
        justPressed.clear()
        justReleased.clear()
    }

    /**
     * Check key state
     */
    override fun getState(key: KeyboardKey): InputState {
        if (key in justPressed) return JUST_PRESSED
        if (key in justReleased) return JUST_RELEASED
        val state = glfwGetKey(window, key.asGLFWInt)
        return if (state == GLFW_PRESS) PRESSED else RELEASED
    }

    override fun destroy() {
        glfwSetKeyCallback(window, null).free()
    }
}