package com.github.germangb.game

import com.github.engine.plugins.heightfield.desktop.DesktopHeightfieldPlugin
import com.github.germangb.engine.assets.AssetLoader
import com.github.germangb.engine.assets.desktop.DesktopAssetLoader
import com.github.germangb.engine.backend.dektop.core.LWJGLContext
import com.github.germangb.engine.backend.dektop.core.LWJGLRuntime
import com.github.germangb.engine.plugin.bullet.BulletPhysics
import com.github.germangb.engine.plugins.assimp.AssimpLoader
import com.github.germangb.engine.plugins.assimp.lwjgl.DesktopAssimpPlugin
import com.github.germangb.engine.plugins.bullet.gdx.DesktopBulletPlugin
import com.github.germangb.engine.plugins.debug.DebugUtils
import com.github.germangb.engine.plugins.debug.desktop.DesktopDebugPlugin
import com.github.germangb.engine.plugins.heightfield.TerrainLoader

fun LWJGLContext.installPlugins() {
    installModule(AssimpLoader.MODULE_NAME, DesktopAssimpPlugin(this))
    installModule(DebugUtils.MODULE_NAME, DesktopDebugPlugin(this))
    installModule(BulletPhysics.MODULE_NAME, DesktopBulletPlugin)
    installModule(AssetLoader.MODULE_NAME, DesktopAssetLoader(this))
    installModule(TerrainLoader.MODULE_NAME, DesktopHeightfieldPlugin(this))
}

fun main(args: Array<String>) {
    val runtime = LWJGLRuntime(720, 480)
    runtime.context.installPlugins()
    runtime.start {
        Testbed(runtime.context)
    }
}