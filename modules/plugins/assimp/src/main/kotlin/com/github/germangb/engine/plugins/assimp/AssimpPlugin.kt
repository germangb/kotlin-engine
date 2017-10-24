package com.github.germangb.engine.plugins.assimp

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin

/**
 * Assimo plugin API
 */
interface AssimpPlugin : Plugin, AssimpLoader

/**
 * For when Assimp plugin is not installed
 */
object UninstalledAssimpPlugin : AssimpLoader {
    override fun loadActor(path: String, manager: AssetManager) = TODO("Assimp is not installed")
    override fun loadAnimations(path: String) = TODO("Assimp is not installed")
}

/**
 * Get assimp loader API
 */
val Context.assimp get() = (getPlugin(AssimpPlugin::class) as? AssimpLoader) ?: UninstalledAssimpPlugin