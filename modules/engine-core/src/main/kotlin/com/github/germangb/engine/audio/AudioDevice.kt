package com.github.germangb.engine.audio

import com.github.germangb.engine.math.Vector3c
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Manage audio
 */
interface AudioDevice {
    /**
     * Set listener orientation (for 3D audio rendering)
     */
    fun setListener(position: Vector3c, look: Vector3c, up: Vector3c)

    /**
     * Create sampled audio (8bit sigled samples)
     */
    fun createSampler(samples: ByteBuffer, sampling: Int, stereo: Boolean = false): Audio

    /**
     * Create sampled audio (16bit signed samples)
     */
    fun createSampler(samples: ShortBuffer, sampling: Int, stereo: Boolean = false): Audio

    /**
     * Create sampled audio (float32 samples)
     */
    fun createSampler(samples: FloatBuffer, sampling: Int, stereo: Boolean = false): Audio

    /**
     * Create audio streamer of Float32 audio
     */
    fun createStream(bufferSize: Int, sampling: Int, stereo: Boolean = false, sampler: FloatAudioDecoder): Audio

    /**
     * Create audio streamer of 16bit audio
     */
    fun createStream(bufferSize: Int, sampling: Int, stereo: Boolean = false, sampler: ShortAudioDecoder): Audio

    /**
     * Create audio streamer of 8bit audio
     */
    fun createStream(bufferSize: Int, sampling: Int, stereo: Boolean = false, sampler: ByteAudioDecoder): Audio
}