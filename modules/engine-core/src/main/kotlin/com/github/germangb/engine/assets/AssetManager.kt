package com.github.germangb.engine.assets

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.core.Destroyable
import com.github.germangb.engine.graphics.*

/**
 * Manage resource loading/unloading
 */
interface AssetManager: Destroyable {
    /**
     * Tell the resource manager to load a texture
     */
    fun loadTexture(path: String, format: TexelFormat, min: TextureFilter, mag: TextureFilter)

    /**
     * Tell the resource manager to load a mesh
     */
    fun loadMesh(path: String, attributes: Set<VertexAttribute>)

    /**
     * Load audio file
     */
    fun loadAudio(path: String)

    /**
     * Check if resource is loaded
     */
    fun isLoaded(path: String): Boolean

    //
    // Loaded resources
    //

    /**
     * Get loaded texture, or null if it is not loaded
     */
    fun getTexture(path: String): Texture?

    /**
     * Get loaded mesh, or null if it is not loaded
     */
    fun getMesh(path: String): Mesh?

    /**
     * Get audio file
     */
    fun getAudio(path: String): Audio?

    //
    // Delegate assets
    //

    /**
     * Delegate texture to AM
     */
    fun delegateTexture(texture: Texture, path: String)

    /**
     * Delegate mesh to AM
     */
    fun delegateMesh(mesh: Mesh, path: String)

    /**
     * Delegate audio to AM
     */
    fun delegateAudio(audio: Audio, path: String)
}
