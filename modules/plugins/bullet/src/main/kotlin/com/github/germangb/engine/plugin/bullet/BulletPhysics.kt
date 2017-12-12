package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Vector3c
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Physics provider interface
 */
interface BulletPhysics {
    companion object {
        val MODULE_NAME = "bullet_physics_engine"
    }

    /** Create a bullet world */
    fun createWorld(gravity: Vector3c): PhysicsWorld

    /** Creates a rigid body */
    fun createRigidBody(mass: Float, motionState: MotionState, shape: PhysicsShape): RigidBody

    /** Create box shape */
    fun createBox(half: Vector3c): PhysicsShape

    /** Create sphere shape */
    fun createShereShape(radius: Float): PhysicsShape

    /** Create capsule shape */
    fun createCapsule(radius: Float, height: Float): PhysicsShape

    /** Create compound shape */
    fun createCompound(): CompoundPhysicsShape

    /** Create heightfield shape */
    fun createHeightfield(width: Int, height: Int, data: ShortBuffer, scale: Float, minHeight: Float, maxHeight: Float): PhysicsShape

    /** Create heightfield shape */
    fun createHeightfield(width: Int, height: Int, data: FloatBuffer, minHeight: Float, maxHeight: Float): PhysicsShape

    /** Create a point 2 point contraint */
    fun createPoint2PointContraint(bodyA: RigidBody, bodyB: RigidBody, pivotA: Vector3c, pivotB: Vector3c): PhysicsContraint

    /** Create a raycast vehicle */
    fun createRaycastVehicle(tuning: VehicleTuning, chasis: RigidBody, world: PhysicsWorld): RaycastVehicle
}