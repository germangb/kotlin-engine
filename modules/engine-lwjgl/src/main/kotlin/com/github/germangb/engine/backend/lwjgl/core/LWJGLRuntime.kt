package com.github.germangb.engine.backend.lwjgl.core

import com.github.germangb.engine.backend.lwjgl.audio.ALAudioDevice
import com.github.germangb.engine.backend.lwjgl.graphics.GLGraphicsDevice
import com.github.germangb.engine.backend.lwjgl.input.GLFWInputDevice
import com.github.germangb.engine.backend.lwjgl.resources.LWJGLAssetLoader
import com.github.germangb.engine.core.Application
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import kotlin.system.exitProcess

class LWJGLRuntime {
    val gfx: GLGraphicsDevice
    val audio: ALAudioDevice
    val res: LWJGLAssetLoader
    val mem: LWJGLBufferManager
    val input: GLFWInputDevice
    val window: Long

    init {
        if (!glfwInit()) {
            System.err.println("Can't init glfw")
            exitProcess(1)
        }

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        window = glfwCreateWindow(640, 480, "OpenGL", 0L, 0L)
        glfwMakeContextCurrent(window)

        GL.createCapabilities()

//        System.err.println("GL_RENDERER=${glGetString(GL_RENDERER)}")
//        System.err.println("GL_VERSION=${glGetString(GL_VERSION)}")
//        System.err.println("GL_VENDOR=${glGetString(GL_VENDOR)}")
//        System.err.println("GL_EXTENSIONS=${glGetString(GL_EXTENSIONS)}")
        glGetError()

        gfx = GLGraphicsDevice(640, 480)
        audio = ALAudioDevice()
        res = LWJGLAssetLoader(audio, gfx)
        mem = LWJGLBufferManager()
        input = GLFWInputDevice(window)

    }

    /**
     * Kickstart LWJGL
     */
    fun start(app: Application) {
        app.init()

        glfwShowWindow(window)
        while (!glfwWindowShouldClose(window)) {
            try {
                audio.updateStreaming()
                app.update()
            } catch (e: Exception) {
                glfwSetWindowShouldClose(window, true)
            } finally {
                input.updateInput()
                glfwPollEvents()
                glfwSwapBuffers(window)
            }
        }

        app.destroy()
        gfx.destroy()
        audio.destroy()
        input.destroy()
        glfwDestroyWindow(window)
        glfwTerminate()
    }
}