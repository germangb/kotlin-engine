package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.collision.CollisionConstants.ACTIVE_TAG
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.*
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.github.germangb.engine.math.*
import com.github.germangb.engine.plugin.bullet.ActivationState
import com.github.germangb.engine.plugin.bullet.RigidBody

class BulletRigidBody(val body: btRigidBody, override val motionState: BulletMotionState) : RigidBody {
    companion object {
        val Int.asEnum get() = ActivationState.values()[this - ACTIVE_TAG]
        val ActivationState.asInt get() = ACTIVE_TAG + ordinal
        private val aux = GdxVector3()
        private val auxMat = GdxMatrix4()
    }

    override var isStatic: Boolean
        get() = (body.collisionFlags and CF_STATIC_OBJECT) != 0
        set(value) {
            if (value) body.collisionFlags = body.collisionFlags or CF_STATIC_OBJECT
            else body.collisionFlags = body.collisionFlags and CF_STATIC_OBJECT.inv()
        }

    override var isKinematic: Boolean
        get() = (body.collisionFlags and CF_KINEMATIC_OBJECT) != 0
        set(value) {
            if (value) body.collisionFlags = body.collisionFlags or CF_KINEMATIC_OBJECT
            else body.collisionFlags = body.collisionFlags and CF_KINEMATIC_OBJECT.inv()
        }

    override var isCharacterObject: Boolean
        get() = (body.collisionFlags and CF_CHARACTER_OBJECT) != 0
        set(value) {
            if (value) body.collisionFlags = body.collisionFlags or CF_CHARACTER_OBJECT
            else body.collisionFlags = body.collisionFlags and CF_CHARACTER_OBJECT.inv()
        }

    var idata = null as Any?

    override var activationState: ActivationState
        get() = body.activationState.asEnum
        set(value) {
            body.activationState = value.asInt
        }

    val linear = Vector3()
    val linearf = Vector3()
    val angularf = Vector3()
    val iaux = Matrix4()

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
        }
}