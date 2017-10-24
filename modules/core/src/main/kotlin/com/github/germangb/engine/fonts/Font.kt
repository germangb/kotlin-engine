package com.github.germangb.engine.fonts

import com.github.germangb.engine.utils.Destroyable
import com.github.germangb.engine.graphics.Texture

/**
 * Font asset
 */
interface Font : Destroyable {
    /**
     * Texture with packed font
     */
    val texture: Texture
}
