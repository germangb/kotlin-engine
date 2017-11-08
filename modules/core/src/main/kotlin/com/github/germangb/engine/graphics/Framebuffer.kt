package com.github.germangb.engine.graphics

import com.github.germangb.engine.utils.Destroyable

/** Resulution/Dimensions of Framebuffer */
data class FramebufferDimensions(val width: Int, val height: Int)

/** Deinifition of a FB texture */
data class FramebufferTextureDef(val index: Int, val width: Int, val height: Int, val format: TexelFormat)

/** Framebuffer textures */
internal val defaulFramebufferDef: GraphicsDevice.(FramebufferTextureDef) -> Texture = {
    createTexture(it.width, it.height, it.format, TextureFilter.LINEAR, TextureFilter.LINEAR)
}

/**
 * Framebuffer stuff
 */
interface Framebuffer: Destroyable {
    /** Framebuffer width */
    val dimensions: FramebufferDimensions

    /** Each of the render targets accesible as a texture */
    val targets: List<Texture>
}