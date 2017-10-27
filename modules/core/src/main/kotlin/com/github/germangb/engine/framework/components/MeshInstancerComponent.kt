package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component
import com.github.germangb.engine.framework.materials.Material
import com.github.germangb.engine.graphics.Mesh

/**
 * Contains mesh instancing information
 */
class MeshInstancerComponent(val mesh: Mesh, val material: Material) : Component()

/**
 * Adds a mesh instancer bind the actor
 */
fun Actor.addMeshInstancer(mesh: Mesh, material: Material) {
    addComponent(MeshInstancerComponent(mesh, material))
}
