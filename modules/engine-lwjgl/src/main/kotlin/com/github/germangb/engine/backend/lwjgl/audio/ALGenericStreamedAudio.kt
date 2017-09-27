package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.GenericAudioDecoder
import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.audio.AudioState
import com.github.germangb.engine.backend.lwjgl.core.ASSERT_CONDITION
import org.lwjgl.openal.AL10.*

/**
 * Generic audio streaming
 */
abstract class ALGenericStreamedAudio(val audio: ALAudioDevice, bufferSize: Int, val sampling: Int, val decoder: GenericAudioDecoder<*>) : Audio {
    companion object {
        val AL_BUFFER_SIZE = 512
        val STREAM_CLOSED = "The audio stream is closed"
    }

    /**
     * Audio state
     */
    private var istate = AudioState.STOPPED

    /**
     * Get stream state
     */
    override val state get() = istate

    /**
     * AL audio buffers
     */
    val buffers = IntArray(bufferSize / AL_BUFFER_SIZE + 1)

    //TODO temporary source
    val source = alGenSources()

    /**
     * Is this sound destroyed?
     */
    private var destroyed = false

    /**
     * Is this streaming stopped?
     */
    private var stopped = true

    init {
        alGenBuffers(buffers)
    }

    /**
     * Initialize buffer
     */
    abstract fun initBuffer()

    /**
     * Empty playback buffer
     */
    private fun emptyBuffer() {
        val processed = alGetSourcei(source, AL_BUFFERS_PROCESSED)
        for (i in 0 until processed) {
            alSourceUnqueueBuffers(source)
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

        alSourcePlay(source)
        istate = AudioState.PLAYING
    }

    override fun pause() {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)

        alSourcePause(source)
        istate = AudioState.PAUSED
    }

    override fun stop() {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)

        if (!stopped) {
            alSourceStop(source)
            istate = AudioState.STOPPED
            stopped = true
            emptyBuffer()
        }
    }

    override fun destroy() {
        if (!destroyed) {
            destroyed = true
            alDeleteBuffers(buffers)
            alDeleteSources(source)
            istate = AudioState.STOPPED
            audio.removeStream(this)
        }
    }

}