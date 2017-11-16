package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.utils.Destroyable

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
    fun addRigidBody(body: RigidBody, group: Short, mask: Short)

    /** Create a box rigid body */
    fun addRigidBody(body: RigidBody)

    /** Adds a constraint to the world */
    fun addConstraint(constraint: PhysicsContraint)

    /** Adds a raycast vehicle to the simulation */
    fun addVehile(vehicle: RaycastVehicle)
}


