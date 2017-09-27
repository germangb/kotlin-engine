package com.github.germangb.game

import com.github.germangb.engine.backend.lwjgl.audio.LwjglAudioAL
import com.github.germangb.engine.backend.lwjgl.core.LwjglBackend
import com.github.germangb.engine.backend.lwjgl.core.LwjglMemoryAllocator
import com.github.germangb.engine.backend.lwjgl.resources.LwjglAssetLoader
import com.github.germangb.engine.backend.lwjgl.core.LwjglRuntime
import com.github.germangb.engine.backend.lwjgl.graphics.LwjglGraphics

fun main(args: Array<String>) {
    val gfx = LwjglGraphics(640, 480)
    val audio = LwjglAudioAL()
    val res = LwjglAssetLoader(audio, gfx)
    val mem = LwjglMemoryAllocator()

    val backend = LwjglBackend(gfx, audio, res, mem)

    val game = GermanGame(backend)
    LwjglRuntime(backend, game).start()
}