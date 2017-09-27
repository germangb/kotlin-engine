package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.GenericAudioDecoder
import com.github.germangb.engine.audio.Sound
import com.github.germangb.engine.audio.SoundState
import com.github.germangb.engine.backend.lwjgl.core.ASSERT_CONDITION
import org.lwjgl.openal.AL10
import org.lwjgl.openal.EXTFloat32

/**
 * Generic audio streaming
 */
abstract class GenericStreamedSound(val audio: LwjglAudioAL, bufferSize: Int, val sampling: Int, val decoder: GenericAudioDecoder<*>) : Sound {
    companion object {
        val AL_BUFFER_SIZE = 512
        val STREAM_CLOSED = "The audio stream is closed"
    }

    /**
     * Sound state
     */
    private var istate = SoundState.STOPPED

    /**
     * Get stream state
     */
    override val state get() = istate

    /**
     * AL audio buffers
     */
    val buffers = IntArray(bufferSize / AL_BUFFER_SIZE + 1)

    //TODO temporary source
    val source = AL10.alGenSources()

    /**
     * Is this sound destroyed?
     */
    private var destroyed = false

    /**
     * Is this streaming stopped?
     */
    private var stopped = true

    init {
        AL10.alGenBuffers(buffers)
    }

    /**
     * Initialize buffer
     */
    abstract fun initBuffer()

    /**
     * Empty playback buffer
     */
    private fun emptyBuffer() {
        val processed = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED)
        for (i in 0 until processed) {
            AL10.alSourceUnqueueBuffers(source)
        }
    }

    /**
     * Fil playback buffer with processed buffers
     */
    abstract fun updateBuffer()

    /**
     * Ensure buffer is always full
     */
    fun updateStreaming() {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)
        updateBuffer()
    }

    override fun play(loop: Boolean) {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)

        if (stopped) {
            decoder.reset()
            initBuffer()
            stopped = false
        } else {
            updateBuffer()
        }

        AL10.alSourcePlay(source)
        istate = SoundState.PLAYING
    }

    override fun pause() {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)

        AL10.alSourcePause(source)
        istate = SoundState.PAUSED
    }

    override fun stop() {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)

        if (!stopped) {
            AL10.alSourceStop(source)
            istate = SoundState.STOPPED
            stopped = true
            emptyBuffer()
        }
    }

    override fun destroy() {
        if (!destroyed) {
            destroyed = true
            AL10.alDeleteBuffers(buffers)
            AL10.alDeleteSources(source)
            istate = SoundState.STOPPED
            audio.removeStream(this)
        }
    }

}