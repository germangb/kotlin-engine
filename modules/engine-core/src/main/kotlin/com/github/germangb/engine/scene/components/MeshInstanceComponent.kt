package com.github.germangb.engine.scene.components

import com.github.germangb.engine.scene.Actor
import com.github.germangb.engine.scene.Component

/**
 * When parented to a MeshInstancerComponent, renders an instance of the mesh
 */
class MeshInstance : Component() {
    override fun init() = Unit
    override fun update() = Unit
}

/**
 * Adds a mesh instance to the actor
 */
fun Actor.addMeshInstance() {
    addComponent(MeshInstance())
}
