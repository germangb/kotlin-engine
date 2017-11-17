package com.github.germangb.engine.plugins.assimp

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.plugins.assimp.AssimpLoader.Companion.MODULE_NAME

/**
 * For when Assimp plugin is not installed
 */
object UninstalledAssimpPlugin : AssimpLoader {
    override fun loadScene(file: FileHandle, manager: AssetManager, flags: Int) = TODO("Assimp is not installed")
}

/**
 * Get assimp loader API
 */
val Context.assimp get() = getModule<AssimpLoader>(MODULE_NAME) ?: UninstalledAssimpPlugin