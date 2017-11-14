package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import com.github.germangb.engine.math.Vector3c
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Bullet plugin API
 */
interface BulletPlugin : Plugin, BulletPhysics

/**
 * For then bullet is not installed
 */
object UninstalledBulletPlugin : BulletPlugin {
    override fun createBox(half: Vector3c) = TODO("Bullet is not installed")

    override fun createShere(radius: Float) = TODO("Bullet is not installed")

    override fun createCapsule(radius: Float, height: Float) = TODO("Bullet is not installed")

    override fun createCompound() = TODO("Bullet is not installed")

    override fun createHeightfield(width: Int, height: Int, data: ShortBuffer, scale: Float, minHeight: Float, maxHeight: Float) = TODO("Bullet is not installed")

    override fun createHeightfield(width: Int, height: Int, data: FloatBuffer, minHeight: Float, maxHeight: Float) = TODO("Bullet is not installed")

    override fun createWorld(gravity: Vector3c) = TODO("Bullet is not installed")

    override fun createPoint2PointContraint(bodyA: RigidBody, bodyB: RigidBody, pivotA: Vector3c, pivotB: Vector3c) = TODO("Bullet is not installed")
}

/**
 * Get bullet plugin
 */
val Context.bullet get() = (getPlugin(BulletPlugin::class) as? BulletPhysics) ?: UninstalledBulletPlugin
