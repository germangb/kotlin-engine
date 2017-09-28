package com.github.germangb.game

import com.github.germangb.engine.audio.FloatAudioDecoder
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Backend
import com.github.germangb.engine.graphics.TestFunction
import com.github.germangb.engine.resources.DumbAssetManager
import com.github.germangb.engine.resources.TextureAsset
import java.nio.FloatBuffer

/**
 * Procedural audio demo
 */
object ProceduralAudio : FloatAudioDecoder {
    var phase = 0
    var modul = 0
    override fun decode(buffer: FloatBuffer): Int {
        // generate samples
        (0 until buffer.limit())
                .map {
                    // modulated frequency
                    val PI2 = 3.141516 * 2
                    val modPhase = 512 + java.lang.Math.sin(0.001 * modul++) * 200
                    java.lang.Math.cos(PI2 * (it + phase) * 512 / 16_000 + modPhase)
                }
                .forEachIndexed { index, d -> buffer.put(index, d.toFloat()) }

        phase += buffer.limit()
        return buffer.limit()
    }

    override fun reset() = Unit
    override val length = -1
}

class GermanGame(val backend: Backend) : Application {
    val manager = DumbAssetManager(backend.assets)

    val audio by lazy {
        val samples = backend.buffers.malloc(16_000 * 2 * 4).asFloatBuffer()

        (0 until samples.capacity())
                .map { java.lang.Math.cos(2 * 3.141592 * it * 520 / 16_000) }
                .forEach { samples.put(it.toFloat()) }

        samples.flip()
        val audio = backend.audio.createAudio(samples, 16000)
        samples.clear()

        backend.buffers.free(samples)
        audio
    }

    val music by lazy {
        backend.assets.loadAudio("music.ogg")
    }

    override fun destroy() {
        audio.destroy()
        music?.destroy()
    }

    override fun init() {
        // load texture
        val tex = TextureAsset(manager, "hellknight.png")

        // vorbis
        music?.play()

        // procedural streamed music
        val procedural = backend.audio.createAudio(ProceduralAudio, 16_000, 16_000, false)
        //procedural.play()
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