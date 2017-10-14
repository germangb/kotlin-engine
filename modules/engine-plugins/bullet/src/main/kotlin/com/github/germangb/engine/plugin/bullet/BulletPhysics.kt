package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Vector3c

/**
 * Physics provider interface
 */
interface BulletPhysics {
    /**
     * Create a bullet world
     */
    fun createWorld(gravity: Vector3c): PhysicsWorld
}