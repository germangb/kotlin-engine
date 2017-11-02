package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.AAB
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.graphics.Mesh

/** Skinned mesh renderer component */
class SkinnedMeshComponent(val root: Actor, val mesh: Mesh, val material: Map<String, Any>)

/** Adds a skinned mesh component */
fun Actor.addSkinnedMesh(root: Actor, mesh: Mesh, material: Map<String, Any>) = addComponent(SkinnedMeshComponent(root, mesh, material))

/** Get skinned mesh */
val Actor.skinnedMesh get() = get(SkinnedMeshComponent::class)

/** Instancer component */
class SkinnedMeshInstancerComponent(val root: Actor, val mesh: Mesh, val material: Map<String, Any>)

/** Adds a skinned component bind the scene */
fun Actor.addSkinnedMeshInstancer(root: Actor, mesh: Mesh, material: Map<String, Any>) = addComponent(SkinnedMeshInstancerComponent(root, mesh, material))

/** Add skinned mesh instancer */
val Actor.skinnedMeshInstancer get() = get(SkinnedMeshInstancerComponent::class)

/** Instance of skinned mesh */
class SkinnedInstanceComponent(val aab: AAB)

/** Adds a skinned mesh component */
fun Actor.addSkinnedInstance(aab: AAB) = addComponent(SkinnedInstanceComponent(aab))

/** Get skinned instance */
val Actor.skinnedInstance get() = get(SkinnedInstanceComponent::class)
