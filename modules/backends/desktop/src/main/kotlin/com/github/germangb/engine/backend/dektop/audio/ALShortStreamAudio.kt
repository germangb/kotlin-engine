package com.github.germangb.engine.backend.dektop.audio

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
        audio.shortBuffer.clear()
        val position = audio.shortBuffer.position()
        streamer.decode(audio.shortBuffer)
        audio.shortBuffer.flip()

        if (audio.shortBuffer.limit() > position) {
            alBufferData(buffer, alFormat, audio.shortBuffer, sampling)
        }

        return audio.shortBuffer.limit() - position
    }
}