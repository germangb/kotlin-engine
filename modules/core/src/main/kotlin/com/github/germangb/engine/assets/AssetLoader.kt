package com.github.germangb.engine.assets

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.fonts.Font
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.graphics.*
import java.io.InputStream

interface AssetLoader {
    /**
     * Load a texture
     */
    fun loadTexture(path: String, format: TexelFormat, min: TextureFilter, mag: TextureFilter): Texture?

    /**
     * Load a mesh
     */
    fun loadMesh(path: String, attributes: Set<VertexAttribute>): Mesh?

    /**
     * Load audio
     */
    fun loadAudio(path: String, stream: Boolean = true): Audio?

    /**
     * Load a font
     */
    fun loadFont(path: String, size: Int, charset: IntRange): Font?

    /**
     * Load a generic resource (InputStream is not managed...)
     */
    fun loadGeneric(path: String): InputStream?
}