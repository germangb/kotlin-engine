package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.ByteAudioDecoder
import org.lwjgl.openal.AL10.*

/**
 * provide 8bit signed audio streaming
 */
class ALByteStreamAudio(audio: ALAudioDevice, bufferSize: Int, sampling: Int, stereo: Boolean, val streamer: ByteAudioDecoder) : ALGenericStreamAudio(audio, bufferSize, sampling, stereo, streamer) {
    /**
     * AL format
     */
    private val alFormat = if (stereo) AL_FORMAT_STEREO16 else AL_FORMAT_MONO16

    /**
     * Fill with 8bit audio
     */
    override fun processBuffer(buffer: Int, size: Int): Int {
        audio.byteBuffer.clear().limit(size)
        val decoded = streamer.decode(audio.byteBuffer)
        audio.byteBuffer.limit(decoded)

        if (decoded > 0) {
            alBufferData(buffer, alFormat, audio.byteBuffer, sampling)
        }
        return decoded
    }
}