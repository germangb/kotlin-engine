package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.GameActor
import com.github.germangb.engine.framework.Component
import com.github.germangb.engine.assets.MeshAsset
import com.github.germangb.engine.assets.TextureAsset

/**
 * Instancer component
 */
class SkinnedMeshInstancerComponent(var root: GameActor, val mesh: MeshAsset, val texture: TextureAsset): Component() {
    override fun init() = Unit
    override fun update() = Unit
    override fun receive(message: Any, callback: (Any) -> Unit) = Unit
}

/**
 * Adds a skinned component to the scene
 * */
fun GameActor.addSkinnedMeshInstancer(root: GameActor, mesh: MeshAsset, texture: TextureAsset) {
    addComponent(SkinnedMeshInstancerComponent(root, mesh, texture))
}
