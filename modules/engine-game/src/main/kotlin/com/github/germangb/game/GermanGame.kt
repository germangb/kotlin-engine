package com.github.germangb.game

import com.github.germangb.engine.audio.FloatAudioStreamer
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Backend
import com.github.germangb.engine.graphics.TestFunction
import com.github.germangb.engine.resources.DumbAssetManager
import com.github.germangb.engine.resources.TextureAsset

/**
 * Procedural audio demo
 */
class ProceduralAudio : FloatAudioStreamer() {
    var phase = 0
    var modul = 0
    override fun invoke(buffer: FloatArray, size: Int) {
        // generate samples
        (0 until size)
                .map {
                    // modulated frequency
                    val PI2 = 3.141516 * 2
                    val modPhase = 512 + java.lang.Math.sin(0.001 * modul++) * 200
                    java.lang.Math.cos(PI2 * (it + phase) * 512 / 16_000 + modPhase)
                }
                .forEachIndexed { index, d -> buffer[index] = d.toFloat() }

        phase += size
    }

}

class GermanGame(val backend: Backend) : Application {
    val manager = DumbAssetManager(backend.resources)

    val audio by lazy {
        val samples = backend.buffers.malloc(16_000 * 2 * 4).asFloatBuffer()

        (0 until samples.capacity())
                .map { java.lang.Math.cos(2 * 3.141592 * it * 520 / 16_000) }
                .forEach { samples.put(it.toFloat()) }

        samples.flip()
        val audio = backend.audio.createSampler(samples, 16000)
        samples.clear()

        backend.buffers.free(samples)
        audio
    }

    override fun destroy() {
        audio.destroy()
    }

    override fun init() {
        // load texture
        val tex = TextureAsset(manager, "hellknight.png")

        // procedural music
        val music = backend.audio.createStream(16000, 16_000, false, ProceduralAudio())
        music.play()
    }

    override fun update() {
        with(backend.graphics) {
            state {
                clearColor(0.2f, 0.2f, 0.2f, 1f)
                clearColorBuffer()
                clearDepthBuffer()
                depthTest(TestFunction.LESS)
            }
        }
    }
}