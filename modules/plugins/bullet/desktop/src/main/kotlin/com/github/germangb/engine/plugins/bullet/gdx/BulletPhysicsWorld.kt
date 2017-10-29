package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.plugin.bullet.PhysicsShape
import com.github.germangb.engine.plugin.bullet.PhysicsWorld
import com.github.germangb.engine.plugin.bullet.RigidBody

class BulletPhysicsWorld(gravity: Vector3c, val bullet: DesktopBulletPlugin) : PhysicsWorld {
    val world: btDynamicsWorld
    val ibodies = mutableListOf<RigidBody>()

    override val bodies get() = ibodies

    companion object {
        val auxMat = GdxMatrix4()
        val auxVec0 = GdxVector3()
        val auxVec1 = GdxVector3()
    }

    /**
     * Ray test callback
     */
    val closestCallback = ClosestRayResultCallback(auxVec0, auxVec1)

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

    override fun rayTestClosest(from: Vector3c, to: Vector3c): RigidBody? {
        val fromGdx = auxVec0.set(from)
        val toGdx = auxVec1.set(to)
        closestCallback.setRayFromWorld(fromGdx)
        closestCallback.setRayToWorld(toGdx)

        // collision test
        world.rayTest(fromGdx, toGdx, closestCallback)
        val collision = closestCallback.collisionObject.userData
        if (collision is RigidBody) return collision
        return null
    }

    override fun createBody(shape: PhysicsShape, fixedRotation: Boolean, mass: Float, friction: Float, restitution: Float, transform: Matrix4c): RigidBody {
        if (shape !is BulletPhysicsShape) throw IllegalArgumentException()
        val collShape = shape.btShape

        // compute inertia momentum
        if (fixedRotation) auxVec0.setZero()
        else collShape.calculateLocalInertia(mass, auxVec0)

        val motionSate = BulletMotionState(Matrix4())
        val btBody = btRigidBody(mass, motionSate, collShape, auxVec0)
        btBody.worldTransform = auxMat.set(transform)
        btBody.restitution = restitution
        btBody.friction = friction
        world.addRigidBody(btBody)

        val rb = BulletRigidBody(this, btBody, motionSate)
        btBody.userData = rb
        ibodies.add(rb)
        return rb
    }
}