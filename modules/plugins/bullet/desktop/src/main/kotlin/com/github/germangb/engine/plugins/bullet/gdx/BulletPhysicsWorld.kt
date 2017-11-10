package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback
import com.badlogic.gdx.physics.bullet.collision.CollisionConstants.DISABLE_DEACTIVATION
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.plugin.bullet.*

private val ID = Matrix4()

class BulletPhysicsWorld(gravity: Vector3c, val bullet: DesktopBulletPlugin) : PhysicsWorld {
    val world: btDynamicsWorld
    val ibodies = mutableListOf<RigidBody>()

    override val bodies get() = ibodies

    companion object {
        val auxMat = GdxMatrix4()
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
            val pos = Vector3(auxVec0.x, auxVec0.y, auxVec0.z)
            return RayTestResult(closestCallback.collisionObject.userData as RigidBody, pos)
        }

        return null
    }

    override fun createBody(shape: PhysicsShape, type: BodyType, mass: Float, group: Int, mask: Int): RigidBody {
        if (shape !is BulletPhysicsShape) throw IllegalArgumentException()
        val collShape = shape.btShape

        // compute inertia momentum
        //if (fixedRotation) auxVec0.setZero()
        //else collShape.calculateLocalInertia(mass, auxVec0)
        collShape.calculateLocalInertia(mass, auxVec0)

        val motionSate = BulletMotionState(Matrix4())
        val btBody = btRigidBody(mass, motionSate, collShape, auxVec0)

        world.addRigidBody(btBody, group.toShort(), mask.toShort())

        // check body type
        if (type == BodyType.KINEMATIC) {
            btBody.collisionFlags = btBody.collisionFlags or CF_KINEMATIC_OBJECT
            btBody.activationState = DISABLE_DEACTIVATION
        }

        val rb = BulletRigidBody(this, btBody, motionSate)
        btBody.userData = rb
        ibodies.add(rb)
        return rb
    }
}