package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.ShortAudioStreamer
import org.lwjgl.openal.AL10

/**
 * rovide Float32 audio streaming
 */
class ShortStreamedSound(audio: LwjglAudioAL, bufferSize: Int, sampling: Int, stereo: Boolean, private val streamer: ShortAudioStreamer) : GenericStreamedSound(audio, bufferSize, sampling, stereo) {
    companion object {
        val AL_BUFFER = ShortArray(1024)
    }

    /**
     * Initialize buffer
     */
    override fun initBuffer() {
        for (alBuffer in buffers) {
            streamer.invoke(AL_BUFFER, AL_BUFFER_SIZE)
            AL10.alBufferData(alBuffer, alFormat, AL_BUFFER, sampling)
            AL10.alSourceQueueBuffers(source, alBuffer)
        }
    }

    /**
     * Fil playback buffer with processed buffers
     */
    override fun updateBuffer() {
        val processed = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED)

        // Reuse processed buffers
        for (i in 0 until processed) {
            val alBuffer = AL10.alSourceUnqueueBuffers(source)
            streamer.invoke(AL_BUFFER, AL_BUFFER_SIZE)
            AL10.alBufferData(alBuffer, alFormat, AL_BUFFER, sampling)
            AL10.alSourceQueueBuffers(source, alBuffer)
        }
    }
}