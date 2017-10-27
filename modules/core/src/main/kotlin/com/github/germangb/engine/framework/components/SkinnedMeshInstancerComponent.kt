package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component
import com.github.germangb.engine.framework.materials.Material
import com.github.germangb.engine.graphics.Mesh

/**
 * Instancer component
 */
class SkinnedMeshInstancerComponent(var root: Actor, val mesh: Mesh, val material: Material) : Component()

/**
 * Adds a skinned component bind the scene
 * */
fun Actor.addSkinnedMeshInstancer(root: Actor, mesh: Mesh, material: Material) {
    addComponent(SkinnedMeshInstancerComponent(root, mesh, material))
}
