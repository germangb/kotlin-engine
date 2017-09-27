package com.github.germangb.engine.backend.lwjgl.audio

/**
 * Audio decoded with Vorbis codec
 */
class VorbisStreamedSound(audio: LwjglAudioAL, sampling: Int, stereo: Boolean, val vorbisDecoder: VorbisAudioDecoder) : ShortStreamedSound(audio, 16_000, sampling, stereo, vorbisDecoder) {
    /**
     * Destroy vorbis decoder
     */
    override fun destroy() {
        super.destroy()
        vorbisDecoder.destroy()
    }
}
