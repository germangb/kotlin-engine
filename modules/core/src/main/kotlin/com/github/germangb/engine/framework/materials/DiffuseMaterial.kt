package com.github.germangb.engine.framework.materials

import com.github.germangb.engine.utils.DummyTexture
import com.github.germangb.engine.graphics.Texture

class DiffuseMaterial : Material() {
    /**
     * Diffuse map
     */
    private var idiffuse: Texture = DummyTexture

    /**
     * Diffuse color texture
     */
    var diffuse: Texture
        get() = idiffuse
        set(value) {
            idiffuse = value
        }
}
