package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin

interface BulletPlugin : Plugin, BulletPhysics

/**
 * Get bullet plugin
 */
val Context.bullet get() = getPlugin(BulletPlugin::class) as? BulletPhysics
