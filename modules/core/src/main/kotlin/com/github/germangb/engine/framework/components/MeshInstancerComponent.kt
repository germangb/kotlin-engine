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
 * Contains mesh information
 */
class MeshComponent(val mesh: Mesh, val material: Material) : Component()

/**
 * Adds a mesh instancer bind the actor
 */
fun Actor.addMeshInstancer(mesh: Mesh, material: Material) {
    addComponent(MeshInstancerComponent(mesh, material))
}

/**
 * Adds a mesh to the actor
 */
fun Actor.addMesh(mesh: Mesh, material: Material) {
    addComponent(MeshInstancerComponent(mesh, material))
}

/**
 * When parented bind a MeshInstancerComponent, renders an instance of the mesh
 */
class MeshInstanceComponent : Component()

/**
 * Adds a mesh instance bind the actor
 */
fun Actor.addMeshInstance() = addComponent(MeshInstanceComponent())
