package com.github.germangb.game

import com.github.germangb.engine.backend.lwjgl.core.LWJGLBackend
import com.github.germangb.engine.backend.lwjgl.core.LWJGLRuntime
import com.github.germangb.engine.plugin.physics.PhysicsPlugin
import com.github.germangb.engine.plugins.physics.bullet.BulletPhysicsPlugin

fun main(args: Array<String>) {
    // init application
    val runtime = LWJGLRuntime(args[1].toInt(), args[3].toInt())
    val backend = LWJGLBackend(runtime)

    // install plugins
    backend.install(PhysicsPlugin::class, BulletPhysicsPlugin)

    // start application
    runtime.start(FontDemo(backend))
}