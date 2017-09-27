package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.audio.AudioState
import com.github.germangb.engine.audio.GenericAudioDecoder
import com.github.germangb.engine.backend.lwjgl.core.ASSERT_CONDITION
import org.lwjgl.openal.AL10.*

/**
 * Generic audio streaming
 */
abstract class ALGenericStreamAudio(val audio: ALAudioDevice, bufferSize: Int, val sampling: Int, val decoder: GenericAudioDecoder<*>) : Audio {
    companion object {
        val AL_BUFFER_SIZE = 512
        val STREAM_CLOSED = "The audio stream is closed"
    }

    /**
     * AL audio buffers
     */
    private val buffers = IntArray(bufferSize / AL_BUFFER_SIZE + 1)

    init {
        alGenBuffers(buffers)
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
     * AL Source
     */
    private val source = alGenSources()

    /**
     * Is this sound destroyed?
     */
    private var destroyed = false

    /**
     * Total decoded samples count
     */
    private var decodedSamples = 0

    /** Set AL buffer data */
    abstract fun fillBufferAL(buffer: Int)

    /**
     * Initialize buffer
     */
    private fun initBuffer() {
        for (alBuffer in buffers) {
            fillBufferAL(alBuffer)
            alSourceQueueBuffers(source, alBuffer)
        }
    }

    /**
     * Fil playback buffer with processed buffers
     */
    private fun updateBuffer() {
        val processed = alGetSourcei(source, AL_BUFFERS_PROCESSED)

        // Reuse processed buffers
        for (i in 0 until processed) {
            val alBuffer = alSourceUnqueueBuffers(source)
            fillBufferAL(alBuffer)
            alSourceQueueBuffers(source, alBuffer)
        }
    }

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
     * Ensure buffer is always full
     */
    fun updateStreaming() {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)

        if (state == AudioState.PLAYING) {
            updateBuffer()

            if (state == AudioState.PLAYING) {
                //TODO check end of stream
                //decoder.length < decodedSamples
            }
        }
    }

    override fun play(loop: Boolean) {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)

        if (state == AudioState.STOPPED) {
            decoder.reset()
            initBuffer()
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

        if (state != AudioState.STOPPED) {
            alSourceStop(source)
            istate = AudioState.STOPPED
            decodedSamples = 0
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