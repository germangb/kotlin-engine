package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.utils.GdxNativesLoader
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.plugin.bullet.BulletPlugin
import com.github.germangb.engine.plugin.bullet.CompoundPhysicsShape
import com.github.germangb.engine.plugin.bullet.PhysicsShape
import com.github.germangb.engine.plugin.bullet.PhysicsWorld
import com.github.germangb.engine.plugins.bullet.gdx.BulletPhysicsWorld.Companion.auxVec0
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

    /**
     * Register a new bullet world
     */
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