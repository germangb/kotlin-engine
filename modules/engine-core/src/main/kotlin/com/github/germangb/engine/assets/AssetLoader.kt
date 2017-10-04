package com.github.germangb.engine.assets

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.fonts.Font
import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.TexelFormat
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.graphics.TextureFilter
import java.io.InputStream

interface AssetLoader {
    /**
     * Load a texture
     */
    fun loadTexture(path: String, format: TexelFormat, min: TextureFilter, mag: TextureFilter): Texture?

    /**
     * Load a mesh
     */
    fun loadMesh(path: String): Mesh?

    /**
     * Load audio
     */
    fun loadAudio(path: String): Audio?

    /**
     * Load a font
     */
    fun loadFont(path: String, size: Int, charset: IntRange): Font?

    /**
     * Load a generic resource (stream is managed by YOU!)
     */
    fun loadGeneric(path: String): InputStream?
}