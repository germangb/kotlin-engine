package com.github.germangb.engine.assets

import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.TexelFormat
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.graphics.TextureFilter

/**
 * Dumb resource manager
 */
class DumbAssetManager(private val loader: AssetLoader): AssetManager {
    /** Loaded textures */
    private val textures = mutableMapOf<String, Texture?>()

    /** Loaded meshes */
    private val meshes = mutableMapOf<String, Mesh?>()

    /**
     * Load a texture
     */
    override fun loadTexture(path: String) {
        textures[path] = loader.loadTexture(path, TexelFormat.RGB8, TextureFilter.LINEAR, TextureFilter.LINEAR)
    }

    /**
     * Get loaded texture
     */
    override fun getTexture(path: String) = textures[path]

    /**
     * Take ownership of texture
     */
    override fun delegateTexture(path: String, texture: Texture) {
        textures[path] = texture
    }

    /**
     * Tell RM to load mesh
     */
    override fun loadMesh(path: String) {
        meshes[path] = loader.loadMesh(path, emptySet())
    }

    /**
     * Get loaded mesh
     */
    override fun getMesh(path: String) = meshes[path]

    override fun delegateMesh(path: String, mesh: Mesh) {
        meshes[path] = mesh
    }

}