package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.GameActor
import com.github.germangb.engine.framework.Component
import com.github.germangb.engine.math.Matrix4c

/**
 * Joint component
 */
class JointComponent(val id: Int, val offset: Matrix4c) : Component() {
    override fun init() = Unit
    override fun update() = Unit
    override fun receive(message: Any, callback: (Any) -> Unit) = Unit
}

/**
 * Adds a joint component to the actor
 */
fun GameActor.addJoint(id: Int, offset: Matrix4c) = addComponent(JointComponent(id, offset))
