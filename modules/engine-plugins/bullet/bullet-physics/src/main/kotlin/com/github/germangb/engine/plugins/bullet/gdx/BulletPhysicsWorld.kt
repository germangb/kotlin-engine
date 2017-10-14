package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.collision.btBoxShape
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
import com.github.germangb.engine.plugin.bullet.PhysicsWorld
import com.github.germangb.engine.plugin.bullet.RigidBody

class BulletPhysicsWorld(val bullet: BulletPhysicsPlugin) : PhysicsWorld {
    /**
     * Bullet dynamics world
     */
    val world: btDynamicsWorld

    init {
        val config = btDefaultCollisionConfiguration()
        val dispatcher = btCollisionDispatcher(config)
        val broadPhase = btDbvtBroadphase()
        val solver = btSequentialImpulseConstraintSolver()
        world = btDiscreteDynamicsWorld(dispatcher, broadPhase, solver, config)
    }

    override fun createBox(mass: Float, restitution: Float, friction: Float, half: Vector3c, transform: Matrix4c): RigidBody {
        val auxVec = GdxVector3()

        // compute inertia momentum
        val shape = btBoxShape(GdxVector3().set(half))
        shape.calculateLocalInertia(mass, auxVec)
        // add body to dynamic local
        val motionSate = BulletMotionState(Matrix4())
        val btBody = btRigidBody(mass, motionSate, shape, auxVec)

        // set body parameters
        val gdxTransf = GdxMatrix4().set(transform)
        btBody.worldTransform = gdxTransf
        btBody.restitution = restitution
        btBody.friction = friction

        world.addRigidBody(btBody)
        return BulletRigidBody(this, btBody, motionSate)
    }

    override fun step(dt: Float) {
        world.stepSimulation(dt)
    }

    override fun destroy() {
        bullet.worlds.remove(this)
    }

}