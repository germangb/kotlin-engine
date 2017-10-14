package com.github.germangb.engine.backend.lwjgl.core

import com.github.germangb.engine.backend.lwjgl.assets.LWJGLAssetLoader
import com.github.germangb.engine.backend.lwjgl.audio.ALAudioDevice
import com.github.germangb.engine.backend.lwjgl.graphics.GLGraphicsDevice
import com.github.germangb.engine.backend.lwjgl.input.GLFWInputDevice
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Plugin
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import kotlin.system.exitProcess

class LWJGLRuntime(width: Int, height: Int) {
    val gfx: GLGraphicsDevice
    val audio: ALAudioDevice
    val res: LWJGLAssetLoader
    val mem: LWJGLBufferManager
    val input: GLFWInputDevice
    val window: Long

    val plugins = mutableListOf<Plugin>()

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
        //glfwWindowHint(GLFW_SAMPLES, 4)
        window = glfwCreateWindow(width, height, "OpenGL", 0L, 0L)
        glfwMakeContextCurrent(window)

        GL.createCapabilities()

        System.err.println("GL_RENDERER=${glGetString(GL_RENDERER)}")
        System.err.println("GL_VERSION=${glGetString(GL_VERSION)}")
        System.err.println("GL_VENDOR=${glGetString(GL_VENDOR)}")
        System.err.println("GL_EXTENSIONS=${glGetString(GL_EXTENSIONS)}")
        glGetError()

        gfx = GLGraphicsDevice(width, height)
        audio = ALAudioDevice()
        res = LWJGLAssetLoader(audio, LWJGLContext(this))
        mem = LWJGLBufferManager()
        input = GLFWInputDevice(window)
    }

    /**
     * Kickstart LWJGL
     */
    fun start(appDef: () -> Application) {
        plugins.forEach { it.onPreInit() }

        val app = appDef.invoke()
        app.init()

        plugins.forEach { it.onPostInit() }

        glfwShowWindow(window)
        while (!glfwWindowShouldClose(window)) {
            try {
                plugins.forEach { it.onPreUpdate() }
                audio.updateStreaming()
                app.update()
                plugins.forEach { it.onPostUpdate() }
            } catch (e: Exception) {
                e.printStackTrace()
                glfwSetWindowShouldClose(window, true)
            } finally {
                input.updateInput()
                glfwPollEvents()
                glfwSwapBuffers(window)
            }
        }

        plugins.forEach { it.onPreDestroy() }
        app.destroy()
        gfx.destroy()
        audio.destroy()
        input.destroy()
        glfwDestroyWindow(window)
        glfwTerminate()
        plugins.forEach { it.onPostDestroy() }
    }
}