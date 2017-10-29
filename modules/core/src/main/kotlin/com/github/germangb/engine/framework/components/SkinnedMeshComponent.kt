package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.AAB
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component
import com.github.germangb.engine.framework.materials.Material
import com.github.germangb.engine.graphics.Mesh

/**
 * Skinned mesh renderer component
 */
class SkinnedMeshComponent(val root: Actor, val mesh: Mesh, val material: Map<String, Any>): Component()

/**
 * Instancer component
 */
class SkinnedMeshInstancerComponent(val root: Actor, val mesh: Mesh, val material: Map<String, Any>) : Component()

/**
 * Adds a skinned component bind the scene
 */
fun Actor.addSkinnedMeshInstancer(root: Actor, mesh: Mesh, material: Map<String, Any>) {
    addComponent(SkinnedMeshInstancerComponent(root, mesh, material))
}

/**
 * Adds a skinned mesh component
 */
fun Actor.addSkinnedMesh(root: Actor, mesh: Mesh, material: Map<String, Any>) {
    addComponent(SkinnedMeshComponent(root, mesh, material))
}

/**
 * Instance of skinned mesh
 */
class SkinnedMeshInstanceComponent(val aab: AAB) : Component()

/**
 * Adds a skinned mesh component
 */
fun Actor.addSkinnedMeshInstance(aab: AAB) = addComponent(SkinnedMeshInstanceComponent(aab))
