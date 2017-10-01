package com.github.germangb.engine.assets

import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.Texture

/**
 * Manage resource loading/unloading
 */
interface AssetManager {
    /**
     * Tell the resource manager to load a texture
     */
    fun loadTexture(path: String)

    /**
     * Get loaded texture, or null if it is not loaded
     */
    fun getTexture(path: String): Texture?

    /**
     * Tell the RM to take ownership of a texture
     */
    fun delegateTexture(path: String, texture: Texture)

    /**
     * Tell the resource manager to load a mesh
     */
    fun loadMesh(path: String)

    /**
     * Get loaded mesh, or null if it is not loaded
     */
    fun getMesh(path: String): Mesh?

    /**
     * Tell TM to take ownership of a mesh
     */
    fun delegateMesh(path: String, mesh: Mesh)
}
