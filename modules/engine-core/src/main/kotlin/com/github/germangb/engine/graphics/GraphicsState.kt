package com.github.germangb.engine.graphics

interface GraphicsState {
    /**
     * Clear color buffer
     */
    fun clearColor(r: Float, g: Float, b: Float, a: Float)

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
}