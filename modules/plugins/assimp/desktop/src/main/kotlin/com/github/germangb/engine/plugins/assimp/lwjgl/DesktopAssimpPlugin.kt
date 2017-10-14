package com.github.germangb.engine.plugins.assimp.lwjgl

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.plugins.assimp.AssimpPlugin

class DesktopAssimpPlugin(val ctx: Context) : AssimpPlugin {
    /**
     * Load assimp scene into Actor blueprint
     */
    override fun loadActor(path: String, manager: AssetManager) = loadActor(path, manager, ctx)
}