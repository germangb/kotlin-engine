package com.github.germangb.engine.audio

/**
 * Provider of audio samples
 */
sealed class GenericAudioDecoder<in T> {
    /**
     * Decode a frame of samples
     */
    abstract fun decode(buffer: T, size: Int)

    /**
     * Called at the beginning of playback.
     */
    abstract fun reset()
}

/**
 * Float32 sampled streamer
 */
abstract class FloatAudioDecoder : GenericAudioDecoder<FloatArray>()

/**
 * 16bit signed sampled streamer
 */
abstract class ShortAudioDecoder : GenericAudioDecoder<ShortArray>()

/**
 * 8bit signed sampled streamer
 */
abstract class ByteAudioDecoder : GenericAudioDecoder<ByteArray>()
