package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component
import com.github.germangb.engine.assets.MeshAsset
import com.github.germangb.engine.assets.TextureAsset

/**
 * Contains mesh instancing information
 */
class MeshInstancerComponent(val mesh: MeshAsset, val texture: TextureAsset) : Component() {
    override fun init() = Unit
    override fun update() = Unit
    override fun receive(message: Any, callback: (Any) -> Unit) = Unit
}

/**
 * Adds a mesh instancer to the actor
 */
fun Actor.addMeshInstancer(mesh: MeshAsset, texture: TextureAsset) {
    addComponent(MeshInstancerComponent(mesh, texture))
}
