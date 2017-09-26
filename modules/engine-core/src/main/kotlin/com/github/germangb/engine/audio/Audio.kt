package com.github.germangb.engine.audio

import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Manage audio
 */
interface Audio {
    /**
     * Create sampled audio (8bit sigled samples)
     */
    fun createSampler(samples: ByteBuffer, sampling: Int, stereo: Boolean = false): Sound

    /**
     * Create sampled audio (16bit signed samples)
     */
    fun createSampler(samples: ShortBuffer, sampling: Int, stereo: Boolean = false): Sound

    /**
     * Create sampled audio (float32 samples)
     */
    fun createSampler(samples: FloatBuffer, sampling: Int, stereo: Boolean = false): Sound

    /**
     * Create audio streamer of Float32 audio
     */
    fun createStream(bufferSize: Int, sampling: Int, stereo: Boolean = false, sampler: FloatAudioStreamer): Sound

    /**
     * Create audio streamer of 16bit audio
     */
    fun createStream(bufferSize: Int, sampling: Int, stereo: Boolean = false, sampler: ShortAudioStreamer): Sound

    /**
     * Create audio streamer of 8bit audio
     */
    fun createStream(bufferSize: Int, sampling: Int, stereo: Boolean = false, sampler: ByteAudioStreamer): Sound
}