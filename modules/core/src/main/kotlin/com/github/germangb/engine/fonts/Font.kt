package com.github.germangb.engine.fonts

import com.github.germangb.engine.core.Destroyable
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
