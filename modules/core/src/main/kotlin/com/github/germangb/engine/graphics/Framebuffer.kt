package com.github.germangb.engine.graphics

import com.github.germangb.engine.utils.Destroyable

/**
 * Framebuffer stuff
 */
interface Framebuffer: Destroyable {
    /** Framebuffer width */
    val width: Int

    /** Heights of the framebuffer */
    val height: Int

    /** Each of the render targets accesible as a texture */
    val targets: List<Texture>
}