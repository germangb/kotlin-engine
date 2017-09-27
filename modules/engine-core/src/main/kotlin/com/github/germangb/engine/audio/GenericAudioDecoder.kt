package com.github.germangb.engine.audio

/**
 * Provider of audio samples
 */
interface GenericAudioDecoder<in T> {
    /**
     * Stream length in samples. A value < 0 means stream has no ending
     */
    val length: Int

    /**
     * Decode a frame of samples
     */
    fun decode(buffer: T, size: Int)

    /**
     * Called at the beginning of playback.
     */
    fun reset()
}

/**
 * Float32 sampled streamer
 */
interface FloatAudioDecoder : GenericAudioDecoder<FloatArray>

/**
 * 16bit signed sampled streamer
 */
interface ShortAudioDecoder : GenericAudioDecoder<ShortArray>

/**
 * 8bit signed sampled streamer
 */
interface ByteAudioDecoder : GenericAudioDecoder<ByteArray>
