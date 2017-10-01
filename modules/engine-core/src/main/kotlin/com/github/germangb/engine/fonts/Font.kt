package com.github.germangb.engine.fonts

import com.github.germangb.engine.core.Destroyable
import com.github.germangb.engine.graphics.Texture

/**
 * Fonts contain gliph information and a texture
 */
interface Font : Destroyable {
    /**
     * Texture with packed gliphs
     */
    val texture: Texture
}