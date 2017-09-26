package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.Sound
import com.github.germangb.engine.backend.lwjgl.core.ASSERT_CONDITION
import org.lwjgl.openal.AL10
import org.lwjgl.openal.EXTFloat32

/**
 * Generic audio streaming
 */
abstract class GenericStreamedSound(val audio: LwjglAudioAL, bufferSize: Int, val sampling: Int, stereo: Boolean) : Sound {
    companion object {
        val AL_BUFFER_SIZE = 1024
        val STREAM_CLOSED = "The audio stream is closed"
    }

    val alFormat = if(stereo) EXTFloat32.AL_FORMAT_STEREO_FLOAT32 else EXTFloat32.AL_FORMAT_MONO_FLOAT32

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
            initBuffer()
            stopped = false
        } else {
            updateBuffer()
        }

        AL10.alSourcePlay(source)
    }

    override fun pause() {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)

        AL10.alSourcePause(source)
    }

    override fun stop() {
        ASSERT_CONDITION(destroyed, STREAM_CLOSED)

        if (!stopped) {
            AL10.alSourceStop(source)
            stopped = true
            emptyBuffer()
        }
    }

    override fun destroy() {
        if (!destroyed) {
            destroyed = true
            AL10.alDeleteBuffers(buffers)
            AL10.alDeleteSources(source)
            //audio.removeStream(this)
        }
    }

}