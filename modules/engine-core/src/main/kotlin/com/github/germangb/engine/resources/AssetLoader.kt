package com.github.germangb.engine.resources

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.Texture
import java.io.InputStream

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
    fun loadAudio(path: String, forceMono: Boolean = false): Audio?

    /**
     * Load a generic resource (stream is managed by YOU!)
     */
    fun loadGeneric(path: String): InputStream?
}