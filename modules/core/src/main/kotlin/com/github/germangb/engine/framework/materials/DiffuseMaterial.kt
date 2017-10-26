package com.github.germangb.engine.framework.materials

import com.github.germangb.engine.utils.DummyTexture
import com.github.germangb.engine.graphics.Texture

class DiffuseMaterial : Material() {
    /**
     * Diffuse property key
     */
    private companion object {
        val DIFFUSE_TEXTURE_KEY = "diffuse_texture"
    }

    /**
     * Diffuse color texture
     */
    var diffuse: Texture
        get() = textures[DIFFUSE_TEXTURE_KEY] ?: DummyTexture
        set(value) {
            this[DIFFUSE_TEXTURE_KEY] = value
        }
}
