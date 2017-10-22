package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.core.Destroyable
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Vector3c
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Dynamics world interface
 */
interface PhysicsWorld : Destroyable {
    /**
     * Bodies currently in world
     */
    val bodies: List<RigidBody>

    /**
     * Get closest rigid body hit by raycast
     */
    fun rayTestClosest(from: Vector3c, to: Vector3c): RigidBody?

    /**
     * Create a box rigid body
     */
    fun createBody(shape: PhysicsShape, fixedRotation: Boolean, mass: Float, friction: Float, restitution: Float, transform: Matrix4c): RigidBody

    /**
     * Create box shape
     */
    fun createBox(half: Vector3c): PhysicsShape

    /**
     * Create sphere shape
     */
    fun createShere(radius: Float): PhysicsShape

    /**
     * Create capsule shape
     */
    fun createCapsule(radius: Float, height: Float): PhysicsShape

    /**
     * Create compound shape
     */
    fun createCompound(): CompoundPhysicsShape

    /**
     * Create heightfield shape
     */
    fun createHeightfield(width: Int, height: Int, data: ShortBuffer, scale: Float, minHeight: Float, maxHeight: Float): PhysicsShape

    /**
     * Create heightfield shape
     */
    fun createHeightfield(width: Int, height: Int, data: FloatBuffer, minHeight: Float, maxHeight: Float): PhysicsShape

    /**
     * Step bullet simulation
     */
    fun stepSimulation(dt: Float)
}