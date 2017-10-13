package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.*
import com.github.germangb.engine.graphics.Mesh

/**
 * Instancer component
 */
class SkinnedMeshInstancerComponent(var root: Actor, val mesh: Mesh, val material: Materialc): Component() {
    override fun init() = Unit
    override fun update() = Unit
    override fun receive(message: Any, callback: (Any) -> Unit) = Unit
}

/**
 * Adds a skinned component to the scene
 * */
fun Actor.addSkinnedMeshInstancer(root: Actor, mesh: Mesh, material: Materialc) {
    addComponent(SkinnedMeshInstancerComponent(root, mesh, material))
}
