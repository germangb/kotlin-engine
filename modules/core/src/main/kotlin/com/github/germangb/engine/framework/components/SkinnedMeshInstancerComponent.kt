package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component
import com.github.germangb.engine.framework.Materialc
import com.github.germangb.engine.graphics.Mesh

/**
 * Instancer component
 */
class SkinnedMeshInstancerComponent(var root: Actor, val mesh: Mesh, val material: Materialc) : Component()

/**
 * Adds a skinned component to the scene
 * */
fun Actor.addSkinnedMeshInstancer(root: Actor, mesh: Mesh, material: Materialc) {
    addComponent(SkinnedMeshInstancerComponent(root, mesh, material))
}
