package com.github.germangb.engine.backend.dektop.core

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL11.glGetError
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryStack

/**
 * Work with stack buffers
 */
fun stackMemory(action: MemoryStack.() -> Unit) {
    val stack = MemoryStack.stackPush()
    try {
        action.invoke(stack)
    } finally {
        stack.pop()
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
        System.err.println("$message ($err : ${err.asGlError()})")
    }
}

/** Convert gl error to readable string */
internal fun Int.asGlError() = when (this) {
    GL_NO_ERROR -> "GL_NO_ERROR"
    GL_INVALID_ENUM -> "GL_INVALID_ENUM"
    GL_INVALID_VALUE -> "GL_INVALID_VALUE"
    GL_INVALID_OPERATION -> "GL_INVALID_OPERATION"
    GL_STACK_OVERFLOW -> "GL_STACK_OVERFLOW"
    GL_STACK_UNDERFLOW -> "GL_STACK_UNDERFLOW"
    GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
    else -> "INVALID_ERROR_CODE"
}

/** Convert fbo status to readable string */
internal fun Int.asFramebufferStatus() = when (this) {
    GL_FRAMEBUFFER_COMPLETE -> "GL_FRAMEBUFFER_COMPLETE"
    GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT"
    GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT"
    GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER -> "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER"
    GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER -> "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER"
    GL_FRAMEBUFFER_UNSUPPORTED -> "GL_FRAMEBUFFER_UNSUPPORTED"
    GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE -> "GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE"
    GL_FRAMEBUFFER_UNDEFINED -> "GL_FRAMEBUFFER_UNDEFINED"
    else -> "INVALID_STATUS_CODE"
}
