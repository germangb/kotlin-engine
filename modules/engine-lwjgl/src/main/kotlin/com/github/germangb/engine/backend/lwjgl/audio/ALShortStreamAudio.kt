package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.ShortAudioDecoder
import org.lwjgl.openal.AL10.*

/**
 * rovide Float32 audio streaming
 */
open class ALShortStreamAudio(audio: ALAudioDevice, bufferSize: Int, sampling: Int, stereo: Boolean, private val streamer: ShortAudioDecoder) : ALGenericStreamAudio(audio, bufferSize, sampling, streamer) {
    companion object {
        val AL_BUFFER = ShortArray(AL_BUFFER_SIZE)
    }

    /**
     * AL format
     */
    private val alFormat = if (stereo) AL_FORMAT_STEREO16 else AL_FORMAT_MONO16

    /**
     * Fill with 16bit audio
     */
    override fun fillBufferAL(buffer: Int) {
        streamer.decode(AL_BUFFER, AL_BUFFER.size)
        alBufferData(buffer, alFormat, AL_BUFFER, sampling)
    }
}