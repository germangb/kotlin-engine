package com.github.germangb.game

import com.github.germangb.engine.backend.lwjgl.core.LWJGLBackend
import com.github.germangb.engine.backend.lwjgl.core.LWJGLRuntime
import com.sampullara.cli.Args
import com.sampullara.cli.Argument

class CommandParser {
    @JvmField
    @Argument(alias = "w", description = "window width", required = true)
    var width = 0

    @JvmField
    @Argument(alias = "h", description = "window height", required = true)
    var height = 0
}

fun main(args: Array<String>) {
//    Configuration.DEBUG.set(true)
//    Configuration.DEBUG_STACK.set(true)
//    Configuration.DEBUG_FUNCTIONS.set(true)
//    Configuration.DEBUG_LOADER.set(true)
//    Configuration.DEBUG_MEMORY_ALLOCATOR.set(true)

    val runtime = LWJGLRuntime(args[1].toInt(), args[3].toInt())
    val backend = LWJGLBackend(runtime)
    runtime.start(FontDemo(backend))
}