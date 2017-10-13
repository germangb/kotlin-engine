package com.github.germangb.engine.physics

import com.github.germangb.engine.math.Vector3c

/**
 * Physics provider interface
 */
interface PhysicsDevice {
    /**
     * Create a physics world
     */
    fun createWorld(gravity: Vector3c): PhysicsWorld
}