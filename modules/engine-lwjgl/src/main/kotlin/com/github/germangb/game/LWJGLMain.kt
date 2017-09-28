package com.github.germangb.game

import com.github.germangb.engine.backend.lwjgl.core.LWJGLBackend
import com.github.germangb.engine.backend.lwjgl.core.LWJGLRuntime
import org.lwjgl.system.Configuration

fun main(args: Array<String>) {
    Configuration.DEBUG.set(true)

    val runtime = LWJGLRuntime()
    val backend = LWJGLBackend(runtime)
    runtime.start(GermanGame(backend))
}