package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.utils.Destroyable

/** Dynamics world interface with functions to spawn rigid bodies and constraints */
interface PhysicsWorld : Destroyable {
    /**
     * Bodies currently in world
     */
    val bodies: List<RigidBody>

    /**
     * Step bullet simulation
     */
    fun stepSimulation(dt: Float)

    /**
     * Get closest rigid body hit by raycast
     */
    fun rayTestClosest(from: Vector3c, to: Vector3c): RigidBody?

    /** Create a box rigid body */
    fun createBody(shape: PhysicsShape, fixedRotation: Boolean, mass: Float, friction: Float, restitution: Float): RigidBody

    /** Create a box rigid body */
    fun createBody(shape: PhysicsShape, fixedRotation: Boolean, mass: Float, friction: Float, restitution: Float, transform: Matrix4c): RigidBody
}


