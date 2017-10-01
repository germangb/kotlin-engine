package com.github.germangb.engine.assets

import com.github.germangb.engine.graphics.Mesh

/**
 * Texture asset
 */
class MeshAsset(manager: AssetManager, val path: String) : GenericAsset<Mesh>(manager) {
    init {
        manager.loadMesh(path)
    }
    /**
     * Get resource from texture
     */
    override val resource get() = manager.getMesh(path)
}