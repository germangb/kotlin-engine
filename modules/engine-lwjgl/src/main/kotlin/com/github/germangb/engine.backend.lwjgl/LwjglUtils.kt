package com.github.germangb.engine.backend.lwjgl

import org.lwjgl.opengl.GL11.GL_NO_ERROR
import org.lwjgl.opengl.GL11.glGetError
import org.lwjgl.system.MemoryStack

/**
 * Work with stack memory
 */
fun stackMemory(action: MemoryStack.() -> Unit) {
    val stack = MemoryStack.stackPush()
    try {
        action.invoke(stack)
    } finally {
        stack.close()
    }
}

/**
 * Check GL error
 */
fun glCheckError(message: String = "No message provided", action: () -> Unit) {
    glGetError()
    action.invoke()
    val err = glGetError()
    if (err != GL_NO_ERROR) {
        System.err.println("$message ($err)")
    }
}