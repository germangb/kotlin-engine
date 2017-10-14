package com.github.germangb.engine.plugins.assimp.lwjgl

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.plugins.assimp.AssimpPlugin

class LwjglAssimpPlugin(val ctx: Context) : AssimpPlugin {
    /**
     * Load assimp scene into Actor blueprint
     */
    override fun loadActor(path: String, manager: AssetManager) = loadActor(path, manager, ctx)

    override fun onPreInit() = Unit
    override fun onPostInit() = Unit
    override fun onPreUpdate() = Unit
    override fun onPostUpdate() = Unit
    override fun onPreDestroy() = Unit
    override fun onPostDestroy() = Unit
}