package com.github.germangb.engine.audio

import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Provider of audio samples
 */
interface GenericAudioDecoder<in T> {
    /**
     * Stream length in samples. A value < 0 means stream has no ending
     */
    val length: Int

    /**
     * Decode a frame of samples. Return the number of decoded samples
     */
    fun decode(buffer: T): Int

    /**
     * Called at the beginning of playback.
     */
    fun reset()
}

/**
 * Float32 sampled streamer
 */
interface FloatAudioDecoder : GenericAudioDecoder<FloatBuffer>

/**
 * 16bit signed sampled streamer
 */
interface ShortAudioDecoder : GenericAudioDecoder<ShortBuffer>

/**
 * 8bit signed sampled streamer
 */
interface ByteAudioDecoder : GenericAudioDecoder<ByteBuffer>
