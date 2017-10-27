package com.github.germangb.engine.plugins.assimp.lwjgl

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.plugins.assimp.AssimpPlugin

class DesktopAssimpPlugin(val ctx: Context) : AssimpPlugin {
    /**
     * Load assimp scene (meshes, animations and a scene blueprint)
     */
    override fun loadScene(file: FileHandle, manager: AssetManager, flags: Int) = loadActor(ctx, file, manager, flags)
}