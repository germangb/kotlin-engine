package com.github.germangb.engine.graphics

interface GraphicsState {
    /**
     * Clear color buffer
     */
    fun clearColor(r: Float, g: Float, b: Float, a: Float)

    /**
     * Clear depth buffer
     */
    fun clearDepth(d: Float)

    /**
     * Clear stencil buffer
     */
    fun clearStencil(s: Int)

    /**
     * Render mode
     */
    fun polygonMode(mode: DrawMode)

    /**
     * Set line width
     */
    fun lineWidth(width: Float)

    /**
     * Set point size
     */
    fun pointSize(size: Float)

    /**
     * Set depth test function
     */
    fun depthTest(func: TestFunction)

    /**
     * Stencil test function
     */
    fun stencilFunc(func: TestFunction, ref: Int, mask: Int)

    /**
     * Stencil operation
     */
    fun stencilOp(sfail: StencilOperation, dfail: StencilOperation, dpass: StencilOperation)

    /**
     * Set culling mode
     */
    fun cullMode(mode: CullMode)

    /**
     * Set viewport
     */
    fun viewPort(x: Int, y: Int, width: Int, height: Int)

    /**
     * Clear color buffer
     */
    fun clearColorBuffer()

    /**
     * Clear depth buffer
     */
    fun clearDepthBuffer()

    /**
     * Clear stencil buffer
     */
    fun clearStencilBuffer()
}