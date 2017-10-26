package com.github.germangb.engine.plugins.assimp.lwjgl

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.plugins.assimp.AnimationData
import com.github.germangb.engine.plugins.assimp.AssimpPlugin

class DesktopAssimpPlugin(val ctx: Context) : AssimpPlugin {
    /**
     * Load assimp scene into Actor blueprint
     */
    override fun loadActor(file: FileHandle, manager: AssetManager) = loadActor(ctx, file, manager, ctx)

    /**
     * Load assimp animation
     */
    override fun loadAnimations(file: FileHandle): AnimationData? = loadAIAnimation(file)
}