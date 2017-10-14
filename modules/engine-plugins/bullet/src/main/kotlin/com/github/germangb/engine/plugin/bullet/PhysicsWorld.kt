package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.core.Destroyable
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Vector3c

/**
 * Dynamics world interface
 */
interface PhysicsWorld : Destroyable {
    /**
     * Create a box rigid body
     */
    fun createBox(mass: Float, restitution: Float, friction: Float, half: Vector3c, transform: Matrix4c): RigidBody

    /**
     * Step bullet simulation
     */
    fun step(dt: Float)
}