package com.github.germangb.engine.graphics

/**
 * Framebuffer stuff
 */
interface Framebuffer {
    /**
     * Framebuffer width
     */
    val width: Int

    /**
     * Framebuffer height
     */
    val height: Int

    /**
     * Get instancing texture
     */
    val targets: List<Texture>
}