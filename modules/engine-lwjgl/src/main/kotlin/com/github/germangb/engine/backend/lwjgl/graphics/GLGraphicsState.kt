package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.CullMode
import com.github.germangb.engine.graphics.GraphicsState
import com.github.germangb.engine.graphics.StencilOperation
import com.github.germangb.engine.graphics.TestFunction
import org.lwjgl.opengl.GL11.*

/**
 * Manage state
 */
class GLGraphicsState : GraphicsState {
    var culling = CullMode.DISABLED
    var depth = TestFunction.DISABLED
    var stencil = TestFunction.DISABLED

    override fun clearColor(r: Float, g: Float, b: Float, a: Float) = glClearColor(r, g, b, a)
    override fun clearDepth(d: Float) = glClearDepth(d.toDouble())
    override fun clearStencil(s: Int) = glClearStencil(s)
    override fun stencilOp(sfail: StencilOperation, dfail: StencilOperation, dpass: StencilOperation) = glStencilOp(sfail.glEnum, dfail.glEnum, dpass.glEnum)

    override fun stencilFunc(func: TestFunction, ref: Int, mask: Int) {
        if (func == TestFunction.DISABLED) {
            glDisable(GL_STENCIL_TEST)
        } else {
            if (stencil == TestFunction.DISABLED) glEnable(GL_STENCIL_TEST)
            glStencilFunc(func.glEnum, ref, mask)
        }
        stencil = func
    }

    override fun depthTest(func: TestFunction) {
        if(func == TestFunction.DISABLED) {
            glDisable(GL_DEPTH_TEST)
        } else {
            if (depth == TestFunction.DISABLED) glEnable(GL_DEPTH_TEST)
            glDepthFunc(func.glEnum)
        }
        depth = func
    }

    override fun cullMode(mode: CullMode) {
        if (mode == CullMode.DISABLED) {
            glDisable(GL_CULL_FACE)
        } else {
            if (culling == CullMode.DISABLED) glEnable(GL_CULL_FACE)
            glCullFace(mode.glEnum)
        }
        culling = mode
    }

    override fun lineWidth(width: Float) = glLineWidth(width)
    override fun pointSize(size: Float) = glPointSize(size)
    override fun viewPort(x: Int, y: Int, width: Int, height: Int) = glViewport(x, y, width, height)
    override fun clearColorBuffer() = glClear(GL_COLOR_BUFFER_BIT)
    override fun clearDepthBuffer() = glClear(GL_DEPTH_BUFFER_BIT)
    override fun clearStencilBuffer() = glClear(GL_STENCIL_BUFFER_BIT)
}