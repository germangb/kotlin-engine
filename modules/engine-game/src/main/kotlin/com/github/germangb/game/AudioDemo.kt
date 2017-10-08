package com.github.germangb.game

import com.github.germangb.engine.audio.FloatAudioDecoder
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Backend
import com.github.germangb.engine.graphics.TestFunction
import com.github.germangb.engine.input.KeyboardKey.*
import com.github.germangb.engine.input.MouseButton.MOUSE_BUTTON_1
import com.github.germangb.engine.input.isJustPressed
import java.nio.FloatBuffer

/**
 * Procedural audio demo
 */
class ProceduralAudio(val backend: Backend) : FloatAudioDecoder {
    var phase = 0
    var modul = 0
    var pitchShiftTarget = 0f
    var pitchShift = 0f

    override fun decode(buffer: FloatBuffer) {
        pitchShiftTarget = backend.input.mouse.x.toFloat()
        //pitchShift += (pitchShiftTarget - pitchShift) * 0.1f

        // generate samples
        (0 until buffer.limit())
                .map {
                    // modulated frequency
                    val PI2 = 3.141516 * 2
                    val modPhase = 512 + java.lang.Math.sin(0.001 * modul++) * 200
                    java.lang.Math.cos(PI2 * (it + phase) * (512 + pitchShift) / 16_000 + modPhase)
                }
                .forEachIndexed { index, d -> buffer.put(d.toFloat()) }

        phase += buffer.limit()
    }

    override fun rewind() = Unit
    override val length = -1
}

class AudioDemo(val backend: Backend) : Application {
    val audio = let {
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

    // procedural streamed music
    val procedural = backend.audio.createAudio(ProceduralAudio(backend), 16_000, 16_000, false)

    val music = let {
        backend.assets.loadAudio("music.ogg")
    }

    override fun destroy() {
        audio.destroy()
        music?.destroy()
    }

    override fun init() {

        // load texture
        //val tex = TextureAsset(manager, "hellknight.png")

        // vorbis
        //music?.play()
    }

    override fun update() {
        if (MOUSE_BUTTON_1.isJustPressed(backend.input)) println("just clicked!")
        if (KEY_P.isJustPressed(backend.input)) procedural.play()
        if (KEY_S.isJustPressed(backend.input)) procedural.stop()
        if (KEY_H.isJustPressed(backend.input)) procedural.pause()

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