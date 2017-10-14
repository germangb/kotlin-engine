package com.github.germangb.engine.plugins.bullet.gdx

import com.github.germangb.engine.plugin.bullet.PhysicsShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape

open class BulletPhysicsShape(val btShape: btCollisionShape) : PhysicsShape {
    /**
     * Free resources
     */
    override fun destroy() = btShape.dispose()
}