package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.FloatAudioDecoder
import org.lwjgl.openal.AL10.alBufferData
import org.lwjgl.openal.EXTFloat32.AL_FORMAT_MONO_FLOAT32
import org.lwjgl.openal.EXTFloat32.AL_FORMAT_STEREO_FLOAT32

/**
 * rovide Float32 audio streaming
 */
class ALFloatStreamAudio(audio: ALAudioDevice, bufferSize: Int, sampling: Int, stereo: Boolean, val streamer: FloatAudioDecoder) : ALGenericStreamAudio(audio, bufferSize, sampling, streamer) {
    companion object {
        val AL_BUFFER = FloatArray(AL_BUFFER_SIZE)
    }

    /**
     * AL format
     */
    private val alFormat = if (stereo) AL_FORMAT_STEREO_FLOAT32 else AL_FORMAT_MONO_FLOAT32

    /**
     * Fill with float32 audio
     */
    override fun fillBufferAL(buffer: Int) {
        streamer.decode(AL_BUFFER, AL_BUFFER.size)
        alBufferData(buffer, alFormat, AL_BUFFER, sampling)
    }
}