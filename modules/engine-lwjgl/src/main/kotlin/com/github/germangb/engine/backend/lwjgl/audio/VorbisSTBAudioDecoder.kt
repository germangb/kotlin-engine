package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.ShortAudioDecoder
import com.github.germangb.engine.core.Destroyable
import org.lwjgl.stb.STBVorbis.*
import java.nio.ShortBuffer

/**
 * Decode Vorbis stream
 */
class VorbisSTBAudioDecoder(val handle: Long, val channels: Int) : Destroyable, ShortAudioDecoder {
    /**
     * Compute length
     */
    override val length = stb_vorbis_stream_length_in_samples(handle)

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
    override fun decode(buffer: ShortBuffer): Int {
        stb_vorbis_get_samples_short_interleaved(handle, channels, buffer)
        return buffer.limit()
    }
}