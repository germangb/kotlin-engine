package com.github.germangb.game

import com.github.germangb.engine.backend.lwjgl.audio.ALAudioDevice
import com.github.germangb.engine.backend.lwjgl.core.LWJGLBackend
import com.github.germangb.engine.backend.lwjgl.core.LWJGLBufferManager
import com.github.germangb.engine.backend.lwjgl.resources.LWJGLAssetLoader
import com.github.germangb.engine.backend.lwjgl.core.LWJGLRuntime
import com.github.germangb.engine.backend.lwjgl.graphics.GLGraphicsDevice
import org.lwjgl.system.Configuration

fun main(args: Array<String>) {
    Configuration.DEBUG.set(true)

    val gfx = GLGraphicsDevice(640, 480)
    val audio = ALAudioDevice()
    val res = LWJGLAssetLoader(audio, gfx)
    val mem = LWJGLBufferManager()

    val backend = LWJGLBackend(gfx, audio, res, mem)

    val game = GermanGame(backend)
    LWJGLRuntime(backend, game).start()
}