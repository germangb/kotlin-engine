package com.github.germangb.engine.assets

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.graphics.*

/**
 * Asset loader API
 */
interface AssetLoader {
    /**
     * Load a texture
     */
    fun loadTexture(file: FileHandle, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean = false): Texture?

    /**
     * Load a mesh
     */
    fun loadMesh(file: FileHandle, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>): Mesh?

    /**
     * Load a mesh
     */
    fun loadMesh(file: FileHandle, usage: MeshUsage, attributes: Array<out VertexAttribute>): Mesh?

    /**
     * Load audio
     */
    fun loadAudio(file: FileHandle, stream: Boolean = true): Audio?
}

