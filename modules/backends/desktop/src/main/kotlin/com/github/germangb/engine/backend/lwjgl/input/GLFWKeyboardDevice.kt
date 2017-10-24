package com.github.germangb.engine.backend.lwjgl.input

import com.github.germangb.engine.utils.Destroyable
import com.github.germangb.engine.input.InputState
import com.github.germangb.engine.input.InputState.*
import com.github.germangb.engine.input.KeyboardDevice
import com.github.germangb.engine.input.KeyboardEvent
import com.github.germangb.engine.input.KeyboardKey
import org.lwjgl.glfw.GLFW.*

class GLFWKeyboardDevice(val window: Long) : KeyboardDevice, Destroyable {
    /**
     * Just pressed keys
     */
    val justPressed = LinkedHashSet<KeyboardKey>()

    /**
     * Just pressed keys
     */
    val justReleased = LinkedHashSet<KeyboardKey>()

    var keyListener: ((KeyboardEvent) -> Unit)? = null

    init {
        glfwSetKeyCallback(window) { _, key, _, action, _ ->
            if (action == GLFW_PRESS) {
                justPressed.add(key.asEnum)
                keyListener?.invoke(KeyboardEvent(key.asEnum, PRESSED))
            } else if (action == GLFW_RELEASE) {
                justReleased.add(key.asEnum)
                keyListener?.invoke(KeyboardEvent(key.asEnum, RELEASED))
            }
        }?.free()
    }

    override fun setListener(listener: ((KeyboardEvent) -> Unit)?) {
        keyListener = listener
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