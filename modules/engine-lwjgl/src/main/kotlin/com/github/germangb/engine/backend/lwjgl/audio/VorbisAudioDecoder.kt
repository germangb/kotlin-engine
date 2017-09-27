package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.ShortAudioDecoder
import com.github.germangb.engine.core.Destroyable
import org.lwjgl.stb.STBVorbis.*

/**
 * Decode Vorbis stream
 */
class VorbisAudioDecoder(val handle: Long, val channels: Int) : Destroyable, ShortAudioDecoder() {
    /**
     * Close and free Vorbis decoder
     */
    override fun destroy() {
        stb_vorbis_close(handle)
    }

    /**
     * Seek start of stream
     */
    override fun reset() {
        stb_vorbis_seek_start(handle)
    }

    /**
     * Decode some samples
     */
    override fun provide(buffer: ShortArray, size: Int) {
        stb_vorbis_get_samples_short_interleaved(handle, channels, buffer)
    }
}