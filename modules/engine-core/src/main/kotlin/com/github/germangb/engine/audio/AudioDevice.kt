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
    fun createAudio(samples: ByteBuffer, sampling: Int, stereo: Boolean = false): Audio

    /**
     * Create sampled audio (16bit signed samples)
     */
    fun createAudio(samples: ShortBuffer, sampling: Int, stereo: Boolean = false): Audio

    /**
     * Create sampled audio (float32 samples)
     */
    fun createAudio(samples: FloatBuffer, sampling: Int, stereo: Boolean = false): Audio

    /**
     * Create audio streamer of Float32 audio
     */
    fun createAudio(sampler: FloatAudioDecoder, bufferSize: Int, sampling: Int, stereo: Boolean = false): Audio

    /**
     * Create audio streamer of 16bit audio
     */
    fun createAudio(sampler: ShortAudioDecoder, bufferSize: Int, sampling: Int, stereo: Boolean = false): Audio

    /**
     * Create audio streamer of 8bit audio
     */
    fun createAudio(sampler: ByteAudioDecoder, bufferSize: Int, sampling: Int, stereo: Boolean = false): Audio
}