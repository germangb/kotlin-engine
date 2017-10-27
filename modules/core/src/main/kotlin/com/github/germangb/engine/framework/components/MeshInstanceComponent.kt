package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component

/**
 * When parented bind a MeshInstancerComponent, renders an instance of the mesh
 */
class MeshInstanceComponent : Component()

/**
 * Adds a mesh instance bind the actor
 */
fun Actor.addMeshInstance() = addComponent(MeshInstanceComponent())
