package com.github.germangb.engine.audio

/**
 * Provider of audio samples
 */
sealed class AudioStreamer<in T> : (T, Int) -> Unit {
    abstract override fun invoke(buffer: T, size: Int)
}

/**
 * Float32 sampled streamer
 */
abstract class FloatAudioStreamer : AudioStreamer<FloatArray>()

/**
 * 16bit signed sampled streamer
 */
abstract class ShortAudioStreamer : AudioStreamer<ShortArray>()

/**
 * 8bit signed sampled streamer
 */
abstract class ByteAudioStreamer : AudioStreamer<ByteArray>()
