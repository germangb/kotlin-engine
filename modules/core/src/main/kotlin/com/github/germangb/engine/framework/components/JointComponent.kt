package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component
import com.github.germangb.engine.math.Matrix4c

/**
 * Joint component
 */
class JointComponent(val id: Int, val offset: Matrix4c) : Component()

/**
 * Adds a joint component bind the actor
 */
fun Actor.addJoint(id: Int, offset: Matrix4c) = addComponent(JointComponent(id, offset))
