package com.github.germangb.engine.graphics

import com.github.germangb.engine.utils.Destroyable

/**
 * Framebuffer stuff
 */
interface Framebuffer: Destroyable {
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