package com.github.germangb.engine.assets

import com.github.germangb.engine.graphics.Texture

/**
 * Texture asset
 */
class TextureAsset(manager: AssetManager, val path: String) : GenericAsset<Texture>(manager) {
    init {
        manager.loadTexture(path)
    }
    /**
     * Get resource from texture
     */
    override val resource get() = manager.getTexture(path)
}