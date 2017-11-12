package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.utils.Destroyable

/** Body type */
enum class BodyType { DYNAMIC, KINEMATIC }

/** Result of ray test */
data class RayTestResult(val body: RigidBody, val position: Vector3c, val normal: Vector3c)

/** Dynamics world interface with functions to spawn rigid bodies and constraints */
interface PhysicsWorld : Destroyable {
    /** Bodies currently in world */
    val bodies: List<RigidBody>

    /** Step bullet simulation */
    fun stepSimulation(dt: Float)

    /** Get closest rigid body hit by raycast */
    fun rayTestClosest(from: Vector3c, to: Vector3c): RayTestResult?

    /** Create a box rigid body */
    fun createBody(shape: PhysicsShape, type: BodyType, mass: Float, group: Int, mask: Int): RigidBody
}


