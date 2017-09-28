package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.audio.AudioState
import com.github.germangb.engine.audio.GenericAudioDecoder
import com.github.germangb.engine.backend.lwjgl.core.ASSERT_CONDITION
import org.lwjgl.openal.AL10.*

/**
 * Generic audio streaming
 */
abstract class ALGenericStreamAudio(val audio: ALAudioDevice, val bufferSize: Int, val sampling: Int, stereo: Boolean, val decoder: GenericAudioDecoder<*>) : Audio {
    companion object {
        val STREAM_CLOSED = "The audio stream is closed"
    }

    /**
     * Audio state
     */
    override val state get() = istate

    private var istate = AudioState.STOPPED
    private val source = alGenSources()
    private var destroyed = false
    private var decodedSamples = 0

    /**
     * Set AL buffer data
     */
    abstract fun processBuffer(buffer: Int, size: Int): Int

    private fun releaseProcessed() {
        val processed = alGetSourcei(source, AL_BUFFERS_PROCESSED)

        // dispose of processed buffers
        for (i in 0 until processed) {
            val alBuffer = alSourceUnqueueBuffers(source)
            audio.bufferQueue.add(alBuffer)
        }

    }

    /**
     * Fil playback buffer with processed buffers
     */
    private fun updateBuffer() {
        releaseProcessed()

        val processed = alGetSourcei(source, AL_BUFFERS_PROCESSED)
        var queued = alGetSourcei(source, AL_BUFFERS_QUEUED)
        //println("P: $processed, Q: $queued")

        while (queued < 2 ) {
            val alBuffer = audio.bufferQueue.poll()
            val decoded = processBuffer(alBuffer, bufferSize)

            if (decoded == 0) {
                // reaching end of stream...
                audio.bufferQueue.push(alBuffer)
                break
            } else {
                if (decodedSamples < decoder.length) {
                    decodedSamples += decoded
                    alSourceQueueBuffers(source, alBuffer)
                } else {
                    audio.bufferQueue.push(alBuffer)
                }
            }

            queued++
        }
    }

    /**
     * Empty playback buffer
     */
    private fun emptyBuffer() {
        val processed = alGetSourcei(source, AL_BUFFERS_QUEUED)
        val buffers = IntArray(processed)
        for (i in 0 until processed) {
            buffers[i] = alSourceUnqueueBuffers(source)
            audio.bufferQueue.push(buffers[i])
        }
    }

    /**
     * Ensure buffer is always full
     */
    fun updateStreaming() {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)

        if (state == AudioState.PLAYING) {
            updateBuffer()

            // check EOS
            if (decodedSamples >= decoder.length) {
                val queued = alGetSourcei(source, AL_BUFFERS_QUEUED)
                if (queued == 0) {
                    istate = AudioState.STOPPED
                }
            }
        }
    }

    override fun play(loop: Boolean) {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)

        if (state == AudioState.STOPPED) {
            decoder.reset()
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
            emptyBuffer()
            alDeleteSources(source)
            istate = AudioState.STOPPED
            audio.removeStream(this)
        }
    }

}