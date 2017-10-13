package com.github.germangb.engine.plugin.physics

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin

interface PhysicsPlugin : Plugin, PhysicsDevice

/**
 * Get physics plugin
 */
val Context.physics get() = getPlugin(PhysicsPlugin::class) as? PhysicsDevice
