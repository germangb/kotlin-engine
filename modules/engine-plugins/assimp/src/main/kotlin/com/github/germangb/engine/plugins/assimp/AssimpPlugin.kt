package com.github.germangb.engine.plugins.assimp

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin

/**
 * Assimo plugin
 */
interface AssimpPlugin : Plugin, AssimpLoader

/**
 * Get assimp loader API
 */
val Context.assimp get() = getPlugin(AssimpPlugin::class) as? AssimpLoader