package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.materials.Material
import com.github.germangb.engine.graphics.Mesh

/** Contains mesh instancing information */
class MeshInstancerComponent(val mesh: Mesh, val material: Map<String, Any>)

/** Adds a mesh instancer bind the actor */
fun Actor.addMeshInstancer(mesh: Mesh, material: Map<String, Any>) = addComponent(MeshInstancerComponent(mesh, material))

/** Get mesh instancer */
val Actor.meshInstancer get() = get(MeshInstancerComponent::class)

/** Contains mesh information */
class MeshComponent(val mesh: Mesh, val material: Material)

/** Adds a mesh to the actor */
fun Actor.addMesh(mesh: Mesh, material: Map<String, Any>) = addComponent(MeshInstancerComponent(mesh, material))

/** Get mesh component */
val Actor.mesh get() = get(MeshComponent::class)

/** When parented bind a MeshInstancerComponent, renders an instance of the mesh */
class MeshInstanceComponent

/** Adds a mesh instance bind the actor */
fun Actor.addMeshInstance() = addComponent(MeshInstanceComponent())

/** Get mesh instance */
val Actor.instance get() = get(MeshInstanceComponent::class)
