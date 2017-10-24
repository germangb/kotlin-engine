package com.github.germangb.engine.audio.desktop

import com.github.germangb.engine.audio.desktop.ShortAudioDecoder
import com.github.germangb.engine.utils.Destroyable
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
    override fun rewind() {
        stb_vorbis_seek_start(handle)
    }

    /**
     * Decode some samples
     */
    override fun decode(buffer: ShortBuffer) {
        val position = stb_vorbis_get_sample_offset(handle)
        stb_vorbis_get_samples_short_interleaved(handle, channels, buffer)
        val decoded = nstb_vorbis_get_sample_offset(handle) - position

        buffer.position(buffer.position() + decoded)
    }
}