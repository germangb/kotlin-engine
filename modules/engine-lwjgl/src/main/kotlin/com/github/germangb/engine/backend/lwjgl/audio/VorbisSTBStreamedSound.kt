package com.github.germangb.engine.backend.lwjgl.audio

/**
 * AudioDevice decoded with Vorbis codec
 */
class VorbisSTBStreamedSound(audio: ALAudioDevice, sampling: Int, stereo: Boolean, val vorbisDecoder: VorbisSTBAudioDecoder) : ALShortStreamedAudio(audio, 16_000, sampling, stereo, vorbisDecoder) {
    /**
     * Destroy vorbis decoder
     */
    override fun destroy() {
        super.destroy()
        vorbisDecoder.destroy()
    }
}
