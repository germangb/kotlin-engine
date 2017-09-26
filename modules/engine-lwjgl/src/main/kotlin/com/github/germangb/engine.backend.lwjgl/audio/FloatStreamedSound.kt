package com.github.germangb.engine.backend.lwjgl.audio

import org.lwjgl.openal.AL10.*

/**
 * rovide Float32 audio streaming
 */
class FloatStreamedSound(audio: LwjglAudioAL, bufferSize: Int, sampling: Int, stereo: Boolean, private val streamer: (FloatArray, Int) -> Unit) : GenericStreamedSound(audio, bufferSize, sampling, stereo) {
    companion object {
        val AL_BUFFER = FloatArray(1024)
    }
    /**
     * Initialize buffer
     */
    override fun initBuffer() {
        for (alBuffer in buffers) {
            streamer.invoke(AL_BUFFER, AL_BUFFER_SIZE)
            alBufferData(alBuffer, alFormat, AL_BUFFER, sampling)
            alSourceQueueBuffers(source, alBuffer)
        }
    }

    /**
     * Fil playback buffer with processed buffers
     */
    override fun updateBuffer() {
        val processed = alGetSourcei(source, AL_BUFFERS_PROCESSED)

        // Reuse processed buffers
        for (i in 0 until processed) {
            val alBuffer = alSourceUnqueueBuffers(source)
            streamer.invoke(AL_BUFFER, AL_BUFFER_SIZE)
            alBufferData(alBuffer, alFormat, AL_BUFFER, sampling)
            alSourceQueueBuffers(source, alBuffer)
        }
    }
}