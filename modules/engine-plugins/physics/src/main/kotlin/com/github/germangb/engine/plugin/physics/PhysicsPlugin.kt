package com.github.germangb.engine.plugin.physics

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import com.github.germangb.engine.math.Vector3c

interface PhysicsPlugin : Plugin, PhysicsDevice

/**
 * Get physics plugin
 */
val Context.physics get() = getPlugin(PhysicsPlugin::class) as? PhysicsDevice

/**
 * Dummy physics
 */
object DummyPhysicsPlugin : PhysicsPlugin {
    override fun createWorld(gravity: Vector3c) = TODO()
    override fun onPreInit() = Unit
    override fun onPostInit()  = Unit
    override fun onPreUpdate() = Unit
    override fun onPostUpdate() = Unit
    override fun onPreDestroy() = Unit
    override fun onPostDestroy() = Unit
}

