package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component
import com.github.germangb.engine.assets.MeshAsset
import com.github.germangb.engine.assets.TextureAsset

/**
 * Instancer component
 */
class SkinnedMeshInstancerComponent(var root: Actor, val mesh: MeshAsset, val texture: TextureAsset): Component() {
    override fun init() = Unit
    override fun update() = Unit

}

/**
 * Adds a skinned component to the scene
 * */
fun Actor.addSkinnedMeshInstancer(root: Actor, mesh: MeshAsset, texture: TextureAsset) {
    addComponent(SkinnedMeshInstancerComponent(root, mesh, texture))
}
