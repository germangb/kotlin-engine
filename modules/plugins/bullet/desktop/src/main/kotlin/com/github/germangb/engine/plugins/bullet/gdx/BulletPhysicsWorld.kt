package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.plugin.bullet.*

class BulletPhysicsWorld(gravity: Vector3c, val bullet: DesktopBulletPlugin) : PhysicsWorld {
    val world: btDynamicsWorld
    val ibodies = mutableListOf<RigidBody>()

    override val bodies get() = ibodies

    companion object {
        val auxVec0 = GdxVector3()
        val auxVec1 = GdxVector3()
    }

    init {
        val config = btDefaultCollisionConfiguration()
        val dispatcher = btCollisionDispatcher(config)
        val broadPhase = btDbvtBroadphase()
        val solver = btSequentialImpulseConstraintSolver()
        world = btDiscreteDynamicsWorld(dispatcher, broadPhase, solver, config)
        world.gravity = auxVec0.set(gravity)
    }

    override fun stepSimulation(dt: Float) {
        world.stepSimulation(dt)
    }

    override fun destroy() {
        bullet.worlds.remove(this)
    }

    override fun rayTestClosest(from: Vector3c, to: Vector3c): RayTestResult? {
        val fromGdx = auxVec0.set(from)
        val toGdx = auxVec1.set(to)
        val closestCallback = ClosestRayResultCallback(fromGdx, toGdx)

        // collision test
        world.rayTest(fromGdx, toGdx, closestCallback)

        if (closestCallback.hasHit()) {
            closestCallback.getHitPointWorld(auxVec0)
            closestCallback.getHitNormalWorld(auxVec1)
            val position = Vector3().set(auxVec0)
            val normal = Vector3().set(auxVec1)
            return RayTestResult(closestCallback.collisionObject.userData as RigidBody, position, normal)
        }

        return null
    }

    override fun addConstraint(constraint: PhysicsContraint) {
        if (constraint !is BulletContraint) throw IllegalArgumentException()
        world.addConstraint(constraint.btConstraint)
    }

    override fun addRigidBody(body: RigidBody) = addRigidBody(body, -1, -1)

    override fun addRigidBody(body: RigidBody, group: Short, mask: Short) {
        if (body !is BulletRigidBody) throw IllegalArgumentException()
        world.addRigidBody(body.body, group, mask)
        ibodies.add(body)
    }

    override fun addVehile(vehicle: RaycastVehicle) {
        if (vehicle !is BulletRaycastVehicle) throw IllegalArgumentException()
        world.addVehicle(vehicle.vehicle)
    }
}