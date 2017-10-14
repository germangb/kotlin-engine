package com.github.germangb.game

import com.github.germangb.engine.backend.lwjgl.core.LWJGLContext
import com.github.germangb.engine.backend.lwjgl.core.LWJGLRuntime
import com.github.germangb.engine.plugin.physics.PhysicsPlugin
import com.github.germangb.engine.plugins.assimp.AssimpPlugin
import com.github.germangb.engine.plugins.assimp.lwjgl.LwjglAssimpPlugin
import com.github.germangb.engine.plugins.physics.bullet.BulletPhysicsPlugin

fun main(args: Array<String>) {
    val runtime = LWJGLRuntime(args[1].toInt(), args[3].toInt())
    val ctx = LWJGLContext(runtime)

    // install plugins
    ctx.install(PhysicsPlugin::class, BulletPhysicsPlugin(ctx))
    ctx.install(AssimpPlugin::class, LwjglAssimpPlugin(ctx))

    // start application
    runtime.start {
        FontDemo(ctx)
    }
}