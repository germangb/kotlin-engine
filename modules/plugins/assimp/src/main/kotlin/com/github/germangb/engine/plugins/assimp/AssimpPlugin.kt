package com.github.germangb.engine.plugins.assimp

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import com.github.germangb.engine.files.FileHandle

/**
 * Assimo plugin API
 */
interface AssimpPlugin : Plugin, AssimpLoader

/**
 * For when Assimp plugin is not installed
 */
object UninstalledAssimpPlugin : AssimpLoader {
    override fun loadActor(file: FileHandle, manager: AssetManager) = TODO("Assimp is not installed")
    override fun loadAnimations(file: FileHandle) = TODO("Assimp is not installed")
}

/**
 * Get assimp loader API
 */
val Context.assimp get() = (getPlugin(AssimpPlugin::class) as? AssimpLoader) ?: UninstalledAssimpPlugin