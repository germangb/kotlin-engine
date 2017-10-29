package com.github.germangb.engine.backend.dektop.audio

import com.github.germangb.engine.audio.FloatAudioDecoder
import org.lwjgl.openal.AL10.alBufferData
import org.lwjgl.openal.EXTFloat32.AL_FORMAT_MONO_FLOAT32
import org.lwjgl.openal.EXTFloat32.AL_FORMAT_STEREO_FLOAT32

/**
 * provide Float32 audio streaming
 */
class ALFloatStreamAudio(audio: ALAudioDevice, bufferSize: Int, sampling: Int, stereo: Boolean, val streamer: FloatAudioDecoder) : ALGenericStreamAudio(audio, bufferSize, sampling, streamer) {
    /**
     * AL format
     */
    private val alFormat = if (stereo) AL_FORMAT_STEREO_FLOAT32 else AL_FORMAT_MONO_FLOAT32

    /**
     * Fill with float32 audio
     */
    override fun processBuffer(buffer: Int, size: Int): Int {
        audio.floatBuffer.clear()
        val position = audio.floatBuffer.position()
        streamer.decode(audio.floatBuffer)
        audio.floatBuffer.flip()

        if (audio.floatBuffer.limit() > position) {
            alBufferData(buffer, alFormat, audio.floatBuffer, sampling)
        }

        return audio.floatBuffer.limit() - position
    }
}