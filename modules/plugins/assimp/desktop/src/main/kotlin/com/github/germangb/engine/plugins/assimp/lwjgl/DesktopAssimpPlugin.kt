package com.github.germangb.engine.plugins.assimp.lwjgl

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.backend.dektop.core.DesktopModule
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.plugins.assimp.AssimpLoader

class DesktopAssimpPlugin(val ctx: Context) : DesktopModule, AssimpLoader {
    /**
     * Load assimp scene (meshes, animations and a scene blueprint)
     */
    override fun loadScene(file: FileHandle, manager: AssetManager, flags: Int) = loadActor(ctx, file, manager, flags)
}