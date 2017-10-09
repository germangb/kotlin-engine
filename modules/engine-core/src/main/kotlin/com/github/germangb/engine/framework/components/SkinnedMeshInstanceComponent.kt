package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component

/**
 * Instance of skinned mesh
 */
class SkinnedMeshInstanceComponent : Component() {
    override fun init() = Unit
    override fun update() = Unit
    override fun receive(message: Any, callback: (Any) -> Unit) = Unit
}

/**
 * Adds a skinned mesh component
 */
fun Actor.addSkinnedMeshInstance() = addComponent(SkinnedMeshInstanceComponent())
