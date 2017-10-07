package com.github.germangb.engine.physics

/**
 * Dynamics world interface
 */
interface PhysicsWorld {
    /**
     * Step physics simulation
     */
    fun step(dt: Float)
}