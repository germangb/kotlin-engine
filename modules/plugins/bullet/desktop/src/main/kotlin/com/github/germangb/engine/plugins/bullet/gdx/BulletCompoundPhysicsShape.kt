package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.collision.btCompoundShape
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.plugin.bullet.CompoundPhysicsShape
import com.github.germangb.engine.plugin.bullet.PhysicsShape

/**
 * Bullet physics compound btShape
 */
class BulletCompoundPhysicsShape(val comp: btCompoundShape) : CompoundPhysicsShape, BulletPhysicsShape(comp) {
    companion object {
        val auxMat = GdxMatrix4()
    }

    /**
     * Add bullet shape bind compound
     */
    override fun addChild(shape: PhysicsShape, local: Matrix4c) {
        if (shape !is BulletPhysicsShape) throw IllegalArgumentException()
        comp.addChildShape(auxMat.set(local), shape.btShape)
    }
}