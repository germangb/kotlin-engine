package com.github.germangb.engine.assets

import com.github.germangb.engine.graphics.TexelFormat
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.graphics.TextureFilter

/**
 * Texture asset
 */
class TextureAsset(manager: AssetManager, val path: String, format: TexelFormat, min: TextureFilter, mag: TextureFilter) : GenericAsset<Texture>(manager) {
    init {
        manager.loadTexture(path, format, min, mag)
    }

    /**
     * Get resource from texture
     */
    override val resource get() = manager.getTexture(path)
}