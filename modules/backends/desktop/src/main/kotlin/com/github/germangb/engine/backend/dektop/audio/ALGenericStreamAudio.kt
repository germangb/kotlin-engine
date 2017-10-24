package com.github.germangb.engine.backend.dektop.audio

import com.github.germangb.engine.audio.AudioState
import com.github.germangb.engine.audio.GenericAudioDecoder
import org.lwjgl.openal.AL10.*

/**
 * Generic audio streaming
 */
abstract class ALGenericStreamAudio(val audio: ALAudioDevice, val bufferSize: Int, val sampling: Int, stereo: Boolean, val decoder: GenericAudioDecoder<*>) : ALAudio(audio) {
    /**
     * Audio state
     */
    override val state get() = istate

    private var istate = AudioState.STOPPED
    private var destroyed = false
    private var decodedSamples = 0

    private var igain = 1f

    override var gain: Float
        get() = igain
        set(value) {
            igain = value
            alSourcef(source, AL_GAIN, value * dev.gain)
        }

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

        //val processed = alGetSourcei(source, AL_BUFFERS_PROCESSED)
        var queued = alGetSourcei(source, AL_BUFFERS_QUEUED)
        //println("P: $processed, Q: $queued")

        while (queued < 2) {
            val alBuffer = audio.bufferQueue.poll()
            val decoded = processBuffer(alBuffer, bufferSize)

            if (decoded == 0) {
                // reaching end of stream...
                audio.bufferQueue.push(alBuffer)
                break
            } else {
                if (decoder.length < 0 || decodedSamples < decoder.length) {
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
        if (state == AudioState.STOPPED) {
            decoder.rewind()
            updateBuffer()
        }

        istate = AudioState.PLAYING
        alSourcePlay(source)
    }

    override fun pause() {
        alSourcePause(source)
        istate = AudioState.PAUSED
    }

    override fun stop() {
        if (state != AudioState.STOPPED) {
            alSourceStop(source)
            istate = AudioState.STOPPED
            decodedSamples = 0
            emptyBuffer()
        }
    }

    override fun destroy() {
        if (!destroyed) {
            super.destroy()
            destroyed = true
            emptyBuffer()
            alDeleteSources(source)
            istate = AudioState.STOPPED
            audio.UNREGISTER_AUDIO(this)
        }
    }

}