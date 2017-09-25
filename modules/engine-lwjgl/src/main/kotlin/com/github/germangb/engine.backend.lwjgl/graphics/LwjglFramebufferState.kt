package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.FramebufferState
import com.github.germangb.engine.graphics.TestFunction
import org.lwjgl.opengl.GL11.*

/**
 * Manage state
 */
class LwjglFramebufferState : FramebufferState {
    override fun clearColor(r: Float, g: Float, b: Float, a: Float) = glClearColor(r, g, b, a)

    override fun depthTest(func: TestFunction) {
        if(func.glEnum < 0) {
            glDisable(GL_DEPTH_TEST)
        } else {
            glEnable(GL_DEPTH_TEST)
            glDepthFunc(func.glEnum)
        }
    }

    override fun clearColorBuffer() = glClear(GL_COLOR_BUFFER_BIT)

    override fun clearDepthBuffer() = glClear(GL_DEPTH_BUFFER_BIT)
}