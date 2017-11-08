package com.github.germangb.engine.assets

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.graphics.*
import com.github.germangb.engine.utils.Destroyable

/**
 * Manage resource loading/unloading
 */
interface AssetManager : Destroyable {
    /**
     * Tell the resource manager bind load a texture
     */
    fun preloadTexture(file: FileHandle, path: String, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean = false)

    /**
     * Tell the resource manager load a mesh
     */
    fun preloadMesh(file: FileHandle, path: String, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>)

    /**
     * Tell the resource manager load a mesh
     */
    fun preloadMesh(file: FileHandle, path: String, usage: MeshUsage, attributes: Array<out VertexAttribute>)

    /**
     * Load audio file
     */
    fun preloadAudio(file: FileHandle, path: String, stream: Boolean = true)

    /**
     * Check if resource is loaded
     */
    fun isLoaded(path: String): Boolean

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

    /**
     * Delegate texture bind AM
     */
    fun delegateTexture(texture: Texture, path: String)

    /**
     * Delegate mesh bind AM
     */
    fun delegateMesh(mesh: Mesh, path: String)

    /**
     * Delegate audio bind AM
     */
    fun delegateAudio(audio: Audio, path: String)
}
