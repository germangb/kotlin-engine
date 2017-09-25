package com.github.germangb.engine.graphics

interface FramebufferState {
    /**
     * Clear color buffer
     */
    fun clearColor(r: Float, g: Float, b: Float, a: Float)

    /**
     * Set depth test function
     */
    fun depthTest(func: TestFunction)

    /**
     * Clear color buffer
     */
    fun clearColorBuffer()

    /**
     * Clear depth buffer
     */
    fun clearDepthBuffer()
}