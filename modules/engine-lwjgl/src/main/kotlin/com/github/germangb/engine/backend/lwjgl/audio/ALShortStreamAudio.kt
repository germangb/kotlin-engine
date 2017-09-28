package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.ShortAudioDecoder
import org.lwjgl.openal.AL10.*

/**
 * provide 16bit signed audio streaming
 */
open class ALShortStreamAudio(audio: ALAudioDevice, bufferSize: Int, sampling: Int, stereo: Boolean, private val streamer: ShortAudioDecoder) : ALGenericStreamAudio(audio, bufferSize, sampling, stereo, streamer) {
    /**
     * AL format
     */
    private val alFormat = if (stereo) AL_FORMAT_STEREO16 else AL_FORMAT_MONO16

    /**
     * Fill with 16bit audio
     */
    override fun processBuffer(buffer: Int, size: Int): Int {
        audio.shortBuffer.clear().limit(size)
        val decoded = streamer.decode(audio.shortBuffer)
        audio.shortBuffer.limit(decoded)

        if (decoded > 0) {
            alBufferData(buffer, alFormat, audio.shortBuffer, sampling)
        }
        return decoded
    }
}