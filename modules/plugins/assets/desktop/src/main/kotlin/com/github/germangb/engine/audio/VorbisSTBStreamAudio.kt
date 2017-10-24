package com.github.germangb.engine.audio

import com.github.germangb.engine.backend.dektop.audio.ALAudioDevice
import com.github.germangb.engine.backend.dektop.audio.ALShortStreamAudio

/**
 * AudioDevice decoded with Vorbis codec
 */
class VorbisSTBStreamAudio(audio: ALAudioDevice, sampling: Int, stereo: Boolean, val vorbisDecoder: VorbisSTBAudioDecoder) : ALShortStreamAudio(audio, 16_000, sampling, stereo, vorbisDecoder) {
    /**
     * Destroy vorbis decoder
     */
    override fun destroy() {
        super.destroy()
        vorbisDecoder.destroy()
    }
}
