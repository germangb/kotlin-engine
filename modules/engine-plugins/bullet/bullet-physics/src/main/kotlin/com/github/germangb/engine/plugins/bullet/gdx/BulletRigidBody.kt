package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.dynamics.*
import com.github.germangb.engine.plugin.bullet.RigidBody

class BulletRigidBody(val world: BulletPhysicsWorld, val body: btRigidBody, val motionState: BulletMotionState) : RigidBody {
    var idata = null as Any?

    override var data: Any?
        get() = idata
        set(value) { idata = value }

    override val transform get() = motionState.matrix

    override fun destroy() {
        world.world.removeRigidBody(body)
    }

}