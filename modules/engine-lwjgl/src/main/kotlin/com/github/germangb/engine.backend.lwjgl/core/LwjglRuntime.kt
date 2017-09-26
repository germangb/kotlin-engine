package com.github.germangb.engine.backend.lwjgl.core

import com.github.germangb.engine.backend.lwjgl.audio.LwjglAudioAL
import com.github.germangb.engine.backend.lwjgl.graphics.LwjglGraphics
import com.github.germangb.engine.core.Application
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import kotlin.system.exitProcess

class LwjglRuntime(val backend: LwjglBackend, val app: Application) {

    /**
     * Kickstart LWJGL
     */
    fun start() {
        if (!glfwInit()) {
            System.err.println("Can't init glfw")
            exitProcess(1)
        }

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        val window = glfwCreateWindow(640, 480, "OpenGL", 0L, 0L)
        glfwMakeContextCurrent(window)

        GL.createCapabilities()

        System.err.println("GL_RENDERER=${glGetString(GL_RENDERER)}")
        System.err.println("GL_VERSION=${glGetString(GL_VERSION)}")
        System.err.println("GL_VENDOR=${glGetString(GL_VENDOR)}")
        System.err.println("GL_EXTENSIONS=${glGetString(GL_EXTENSIONS)}")
        glGetError()

        app.init()

        glfwShowWindow(window)
        while (!glfwWindowShouldClose(window)) {
            try {
                (backend.audio as LwjglAudioAL).updateStreaming()
                app.update()
            } catch (e: Exception) {
                glfwSetWindowShouldClose(window, true)
            } finally {
                glfwPollEvents()
                glfwSwapBuffers(window)
            }
        }

        app.destroy()
        (backend.graphics as LwjglGraphics).destroy()
        (backend.audio as LwjglAudioAL).destroy()
        glfwDestroyWindow(window)
        glfwTerminate()
    }
}