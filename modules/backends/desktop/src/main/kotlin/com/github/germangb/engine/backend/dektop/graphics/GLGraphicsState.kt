package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.graphics.*
import org.lwjgl.opengl.GL11.*

/**
 * Manage state
 */
class GLGraphicsState : GraphicsState {
    override fun clearColor(r: Float, g: Float, b: Float, a: Float) = glClearColor(r, g, b, a)
    override fun clearDepth(d: Float) = glClearDepth(d.toDouble())
    override fun clearStencil(s: Int) = glClearStencil(s)
    override fun stencilOp(sfail: StencilOperation, dfail: StencilOperation, dpass: StencilOperation) = glStencilOp(sfail.glEnum, dfail.glEnum, dpass.glEnum)

    override fun renderMode(mode: RenderMode) {
        glPolygonMode(GL_FRONT_AND_BACK, mode.glEnum)
    }

    override fun stencilFunc(func: TestFunction, ref: Int, mask: Int) {
        if (func == TestFunction.DISABLED) {
            glDisable(GL_STENCIL_TEST)
        } else {
            glEnable(GL_STENCIL_TEST)
            glStencilFunc(func.glEnum, ref, mask)
        }
    }

    override fun depthTest(func: TestFunction) {
        if(func == TestFunction.DISABLED) {
            glDisable(GL_DEPTH_TEST)
        } else {
            glEnable(GL_DEPTH_TEST)
            glDepthFunc(func.glEnum)
        }
    }

    override fun cullMode(mode: CullMode) {
        if (mode == CullMode.DISABLED) {
            glDisable(GL_CULL_FACE)
        } else {
            glEnable(GL_CULL_FACE)
            glCullFace(mode.glEnum)
        }
    }

    override fun lineWidth(width: Float) = glLineWidth(width)
    override fun pointSize(size: Float) = glPointSize(size)
    override fun viewPort(x: Int, y: Int, width: Int, height: Int) = glViewport(x, y, width, height)
    override fun clearColorBuffer() = glClear(GL_COLOR_BUFFER_BIT)
    override fun clearDepthBuffer() = glClear(GL_DEPTH_BUFFER_BIT)
    override fun clearStencilBuffer() = glClear(GL_STENCIL_BUFFER_BIT)
}