package com.github.germangb.engine.backend.dektop.core

import com.github.germangb.engine.backend.dektop.audio.ALAudioDevice
import com.github.germangb.engine.backend.dektop.files.DesktopFiles
import com.github.germangb.engine.backend.dektop.graphics.GLGraphicsDevice
import com.github.germangb.engine.backend.dektop.input.GLFWInputDevice
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Plugin
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.MemoryUtil.NULL
import kotlin.system.exitProcess

class LWJGLRuntime(width: Int, height: Int) {
    val gfx: GLGraphicsDevice
    val audio: ALAudioDevice
    val mem: LWJGLBufferManager
    val input: GLFWInputDevice
    val files: DesktopFiles
    val time: GLFWTime
    val context: LWJGLContext
    val window: Long

    var cursorHidden = false
    var depthBits = 24
    var stencilBits = 8
    var samples = 0

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
        glfwWindowHint(GLFW_DEPTH_BITS, depthBits)
        glfwWindowHint(GLFW_STENCIL_BITS, stencilBits)
        glfwWindowHint(GLFW_SAMPLES, samples)
        window = glfwCreateWindow(width, height, "OpenGL", NULL, NULL)
        glfwMakeContextCurrent(window)
        glfwSwapInterval(1)

        GL.createCapabilities()
        //debug
        val debug = System.getProperty("com.github.germangb.engine.debug", "false")
        if (debug.toBoolean()) {
            GLUtil.setupDebugMessageCallback()
        }

        System.err.println("GL_RENDERER=${glGetString(GL_RENDERER)}")
        System.err.println("GL_VERSION=${glGetString(GL_VERSION)}")
        System.err.println("GL_VENDOR=${glGetString(GL_VENDOR)}")
        System.err.println("GL_EXTENSIONS=${glGetString(GL_EXTENSIONS)}")
        glGetError()

        files = DesktopFiles()
        gfx = GLGraphicsDevice(files, width, height)
        audio = ALAudioDevice()
        mem = LWJGLBufferManager()
        input = GLFWInputDevice(window, width, height)
        time = GLFWTime()

        context = LWJGLContext(this)
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

        if(cursorHidden) glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)

        // reset elapsed
        glfwSetTime(0.0)
        var lastTime = 0f

        var lastFpsTime = 0f
        var ticks = 0

        while (!glfwWindowShouldClose(window)) {
            val now = glfwGetTime().toFloat()
            time._elapsed = now
            time._delta = now - lastTime
            lastTime = now

            if (now - lastFpsTime > 1) {
                time._fps = ticks
                lastFpsTime = now
                ticks = 0
            } else ticks++

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