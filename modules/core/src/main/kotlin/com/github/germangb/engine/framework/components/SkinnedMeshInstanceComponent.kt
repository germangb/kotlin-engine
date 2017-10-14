package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.AAB
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component

/**
 * Instance of skinned mesh
 */
class SkinnedMeshInstanceComponent(val aab: AAB) : Component() {
    override fun init() = Unit
    override fun update() = Unit
    override fun receive(message: Any, callback: (Any) -> Unit) = Unit
}

/**
 * Adds a skinned mesh component
 */
fun Actor.addSkinnedMeshInstance(aab: AAB) = addComponent(SkinnedMeshInstanceComponent(aab))
