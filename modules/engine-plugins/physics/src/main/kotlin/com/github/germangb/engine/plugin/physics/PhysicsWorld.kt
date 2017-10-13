package com.github.germangb.engine.plugin.physics

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
    fun createBox(half: Vector3c, transform: Matrix4c): RigidBody

    /**
     * Step physics simulation
     */
    fun step(dt: Float)
}