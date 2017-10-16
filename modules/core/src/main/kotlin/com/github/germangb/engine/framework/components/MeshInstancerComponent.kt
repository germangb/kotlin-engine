package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component
import com.github.germangb.engine.framework.Materialc
import com.github.germangb.engine.graphics.Mesh

/**
 * Contains mesh instancing information
 */
class MeshInstancerComponent(val mesh: Mesh, val material: Materialc) : Component()

/**
 * Adds a mesh instancer to the actor
 */
fun Actor.addMeshInstancer(mesh: Mesh, material: Materialc) {
    addComponent(MeshInstancerComponent(mesh, material))
}
