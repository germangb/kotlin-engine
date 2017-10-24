package com.github.germangb.engine.assets

import com.github.germangb.engine.audio.desktop.Audio
import com.github.germangb.engine.graphics.*

/**
 * Asset loader API
 */
interface AssetLoader {
    /**
     * Load a texture
     */
    fun loadTexture(path: String, format: TexelFormat, min: TextureFilter, mag: TextureFilter): Texture?

    /**
     * Load a mesh
     */
    fun loadMesh(path: String, usage: MeshUsage, vararg attributes: VertexAttribute): Mesh?

    /**
     * Load audio
     */
    fun loadAudio(path: String, stream: Boolean = true): Audio?
}

