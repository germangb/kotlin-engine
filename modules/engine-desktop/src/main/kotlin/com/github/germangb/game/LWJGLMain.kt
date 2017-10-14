package com.github.germangb.game

import com.github.germangb.engine.backend.lwjgl.core.LWJGLContext
import com.github.germangb.engine.backend.lwjgl.core.LWJGLRuntime
import com.github.germangb.engine.plugin.bullet.BulletPlugin
import com.github.germangb.engine.plugins.assimp.AssimpPlugin
import com.github.germangb.engine.plugins.assimp.lwjgl.LwjglAssimpPlugin
import com.github.germangb.engine.plugins.bullet.gdx.BulletPhysicsPlugin
import com.github.germangb.engine.plugins.stb.STBPlugin
import com.github.germangb.engine.plugins.stb.lwjgl.LWJGLSTBPlugin

fun main(args: Array<String>) {
    val runtime = LWJGLRuntime(args[1].toInt(), args[3].toInt())
    val ctx = LWJGLContext(runtime)

    // installed plugins
    ctx.install(AssimpPlugin::class, LwjglAssimpPlugin(ctx))
    ctx.install(BulletPlugin::class, BulletPhysicsPlugin)
    ctx.install(STBPlugin::class, LWJGLSTBPlugin)

    // start application
    runtime.start {
        FontDemo(ctx)
    }
}