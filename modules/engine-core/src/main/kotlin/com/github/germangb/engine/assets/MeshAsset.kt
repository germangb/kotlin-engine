package com.github.germangb.engine.assets

import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.VertexAttribute

/**
 * Texture asset
 */
class MeshAsset(manager: AssetManager, val path: String, attributes: Set<VertexAttribute>) : GenericAsset<Mesh>(manager) {
    init {
        manager.loadMesh(path, attributes)
    }

    /**
     * Get resource from texture
     */
    override val resource get() = manager.getMesh(path)
}