package com.github.germangb.engine.resources

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.framework.Actor

interface AssetLoader {
    /**
     * Load a texture
     */
    fun loadTexture(path: String): Texture?

    /**
     * Load a mesh
     */
    fun loadMesh(path: String): Mesh?

    /**
     * Load audio
     */
    fun loadSound(path: String): Audio?
}