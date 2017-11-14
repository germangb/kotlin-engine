package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState
import com.github.germangb.engine.math.*
import com.github.germangb.engine.plugin.bullet.MotionState
import com.github.germangb.engine.plugin.bullet.RigidBody

private val aux = GdxVector3()
private val auxMat = GdxMatrix4()

class BulletRigidBody(val world: BulletPhysicsWorld, val body: btRigidBody, ms: btMotionState) : RigidBody {
    var idata = null as Any?
    val linear = Vector3()
    val linearf = Vector3()
    val angularf = Vector3()
    val iaux = Matrix4()

    override val motionState = BulletMotionState(ms)

    override fun activate() {
        body.activate()
    }

    override var friction: Float
        get() = body.friction
        set(value) {
            body.friction = value
        }

    override var linearFactor: Vector3c
        get() = linearf
        set(value) {
            linearf.set(value)
            body.linearFactor = aux.set(value)
        }

    override var angularFactor: Vector3c
        get() = angularf
        set(value) {
            angularf.set(value)
            body.angularFactor = aux.set(value)
        }

    override var restitution: Float
        get() = body.restitution
        set(value) {
            body.restitution = value
        }

    override var data: Any?
        get() = idata
        set(value) {
            idata = value
        }

    override var velocity: Vector3c
        get() {
            val v = body.linearVelocity
            linear.x = v.x
            linear.y = v.y
            linear.z = v.z
            return linear
        }
        set(value) {
            aux.x = value.x
            aux.y = value.y
            aux.z = value.z
            body.linearVelocity = aux
        }

    override fun clearForces() {
        body.clearForces()
    }

    override var transform: Matrix4c
        get() = iaux.set(body.worldTransform)
        set(value) {
            auxMat.set(value)
            body.worldTransform = auxMat
            //body.motionState.setWorldTransform(auxMat)
        }

    override fun destroy() {
        world.ibodies.remove(this)
        world.world.removeRigidBody(body)
    }

}