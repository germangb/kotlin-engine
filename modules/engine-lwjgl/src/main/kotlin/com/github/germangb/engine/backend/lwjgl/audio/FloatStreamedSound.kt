package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.FloatAudioDecoder
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.EXTFloat32.*

/**
 * rovide Float32 audio streaming
 */
class FloatStreamedSound(audio: LwjglAudioAL, bufferSize: Int, sampling: Int, stereo: Boolean, val streamer: FloatAudioDecoder) : GenericStreamedSound(audio, bufferSize, sampling, streamer) {
    companion object {
        val AL_BUFFER = FloatArray(AL_BUFFER_SIZE)
    }

    /**
     * AL format
     */
    private val alFormat = if(stereo) AL_FORMAT_STEREO_FLOAT32 else AL_FORMAT_MONO_FLOAT32

    /**
     * Initialize buffer
     */
    override fun initBuffer() {
        for (alBuffer in buffers) {
            streamer.decode(AL_BUFFER, AL_BUFFER.size)
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
            streamer.decode(AL_BUFFER, AL_BUFFER_SIZE)
            alBufferData(alBuffer, alFormat, AL_BUFFER, sampling)
            alSourceQueueBuffers(source, alBuffer)
        }
    }
}