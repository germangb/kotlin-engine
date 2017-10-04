package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.GameActor
import com.github.germangb.engine.framework.Component

/**
 * When parented to a MeshInstancerComponent, renders an instance of the mesh
 */
class MeshInstance : Component() {
    override fun init() = Unit
    override fun update() = Unit
    override fun receive(message: Any, callback: (Any) -> Unit) = Unit
}

/**
 * Adds a mesh instance to the actor
 */
fun GameActor.addMeshInstance() = addComponent(MeshInstance())
