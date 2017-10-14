package com.github.germangb.game

import com.github.germangb.engine.backend.lwjgl.core.LWJGLContext
import com.github.germangb.engine.backend.lwjgl.core.LWJGLRuntime
import com.github.germangb.engine.plugin.bullet.BulletPlugin
import com.github.germangb.engine.plugins.assimp.AssimpPlugin
import com.github.germangb.engine.plugins.assimp.lwjgl.DesktopAssimpPlugin
import com.github.germangb.engine.plugins.bullet.gdx.DesktopBulletPlugin
import com.github.germangb.engine.plugins.debug.DebugPlugin
import com.github.germangb.engine.plugins.debug.desktop.DesktopDebugPlugin

fun LWJGLContext.installPlugins() {
    install(AssimpPlugin::class, DesktopAssimpPlugin(this))
    install(DebugPlugin::class, DesktopDebugPlugin(this))
    install(BulletPlugin::class, DesktopBulletPlugin)
}

fun main(args: Array<String>) {
    val runtime = LWJGLRuntime(640, 480)
    val ctx = LWJGLContext(runtime)
    ctx.installPlugins()
    runtime.start { FontDemo(ctx) }
}