package com.github.germangb.game

import com.github.germangb.engine.backend.lwjgl.LwjglBackend
import com.github.germangb.engine.backend.lwjgl.LwjglMemoryAllocator
import com.github.germangb.engine.backend.lwjgl.LwjglResourceLoader
import com.github.germangb.engine.backend.lwjgl.LwjglRuntime
import com.github.germangb.engine.backend.lwjgl.graphics.LwjglGraphics

fun main(args: Array<String>) {
    val gfx = LwjglGraphics()
    val res = LwjglResourceLoader(gfx)
    val mem = LwjglMemoryAllocator()
    val backend = LwjglBackend(gfx, res, mem)
    LwjglRuntime(GermanGame(backend)).start()
}