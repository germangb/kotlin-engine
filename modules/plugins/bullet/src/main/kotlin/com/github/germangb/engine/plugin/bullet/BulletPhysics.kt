package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Vector3c
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Physics provider interface
 */
interface BulletPhysics {
    /**
     * Create a bullet world
     */
    fun createWorld(gravity: Vector3c): PhysicsWorld

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
}