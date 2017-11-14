package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btPoint2PointConstraint
import com.badlogic.gdx.utils.GdxNativesLoader
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.plugin.bullet.*
import com.github.germangb.engine.plugins.bullet.gdx.BulletPhysicsWorld.Companion.auxVec0
import com.github.germangb.engine.plugins.bullet.gdx.BulletPhysicsWorld.Companion.auxVec1
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Bullet bullet plugin implementation
 */
object DesktopBulletPlugin : BulletPlugin {
    val worlds = mutableListOf<BulletPhysicsWorld>()

    override fun onPreInit() {
        GdxNativesLoader.load()
        Bullet.init()
    }

    override fun createPoint2PointContraint(bodyA: RigidBody, bodyB: RigidBody, pivotA: Vector3c, pivotB: Vector3c): PhysicsContraint {
        if (bodyA !is BulletRigidBody) throw IllegalArgumentException()
        if (bodyB !is BulletRigidBody) throw IllegalArgumentException()
        val p2p = btPoint2PointConstraint(bodyA.body, bodyB.body, auxVec0.set(pivotA), auxVec1.set(pivotB))
        return BulletContraint(p2p)
    }

    override fun createWorld(gravity: Vector3c): PhysicsWorld {
        val world = BulletPhysicsWorld(gravity, this)
        worlds.add(world)
        return world
    }

    override fun createHeightfield(width: Int, height: Int, data: ShortBuffer, scale: Float, minHeight: Float, maxHeight: Float): PhysicsShape {
        val upAxis = 1
        val shape = btHeightfieldTerrainShape(width, height, data, scale, minHeight, maxHeight, upAxis, false)
        return BulletPhysicsShape(shape)
    }

    override fun createHeightfield(width: Int, height: Int, data: FloatBuffer, minHeight: Float, maxHeight: Float): PhysicsShape {
        val upAxis = 1
        val shape = btHeightfieldTerrainShape(width, height, data, 0f, minHeight, maxHeight, upAxis, false)
        return BulletPhysicsShape(shape)
    }

    override fun createBox(half: Vector3c) = BulletPhysicsShape(btBoxShape(auxVec0.set(half)))

    override fun createShere(radius: Float) = BulletPhysicsShape(btSphereShape(radius))

    override fun createCapsule(radius: Float, height: Float) = BulletPhysicsShape(btCapsuleShape(radius, height))

    override fun createCompound(): CompoundPhysicsShape = BulletCompoundPhysicsShape(btCompoundShape())

}