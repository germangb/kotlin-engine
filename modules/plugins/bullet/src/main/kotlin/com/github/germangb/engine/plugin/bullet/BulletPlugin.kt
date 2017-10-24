package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import com.github.germangb.engine.math.Vector3c

/**
 * Bullet plugin API
 */
interface BulletPlugin : Plugin, BulletPhysics

/**
 * For then bullet is not installed
 */
object UninstalledBulletPlugin : BulletPlugin {
    override fun createWorld(gravity: Vector3c) = TODO("Bullet is not installed")
}

/**
 * Get bullet plugin
 */
val Context.bullet get() = (getPlugin(BulletPlugin::class) as? BulletPhysics) ?: UninstalledBulletPlugin
