package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.*
import com.github.germangb.engine.backend.lwjgl.core.stackMemory
import com.github.germangb.engine.core.Destroyable
import com.github.germangb.engine.math.Vector3c
import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.openal.ALCapabilities
import org.lwjgl.openal.EXTFloat32.AL_FORMAT_MONO_FLOAT32
import org.lwjgl.openal.EXTFloat32.AL_FORMAT_STEREO_FLOAT32
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

/**
 * OpenAL audio
 */
class ALAudioDevice : AudioDevice, Destroyable {
    /**
     * Set OpenAL listener orientation
     */
    override fun setListener(position: Vector3c, look: Vector3c, up: Vector3c) {
        stackMemory {
            val orientation = mallocFloat(9)
            position.get(orientation).position(3)
            look.get(orientation).position(6)
            up.get(orientation).position(9).flip()
            alListenerfv(AL_ORIENTATION, orientation)
        }
    }

    /** AL audio capabilities */
    private val alCaps: ALCapabilities

    /**
     * AudioDevice device
     */
    private val device: Long = alcOpenDevice(null as ByteBuffer?)

    /**
     * ALC context
     */
    private val ctx: Long

    init {
        val alcCaps = ALC.createCapabilities(device)
        ctx = alcCreateContext(device, null as IntBuffer?)
        alcMakeContextCurrent(ctx)
        alCaps = AL.createCapabilities(alcCaps)

        System.err.println("AL_VENDOR=${alGetString(AL_VENDOR)}")
        System.err.println("AL_VERSION=${alGetString(AL_VERSION)}")
        System.err.println("AL_RENDERER=${alGetString(AL_RENDERER)}")
        System.err.println("AL_EXTENSIONS=${alGetString(AL_EXTENSIONS)}")

        // reset error
        alGetError()
    }

    override fun destroy() {
        alcDestroyContext(ctx)
        alcCloseDevice(device)
    }

    override fun createSampler(samples: ByteBuffer, sampling: Int, stereo: Boolean): Audio {
        val buffer = alGenBuffers()
        val format = if (stereo) AL_FORMAT_STEREO8 else AL_FORMAT_MONO8
        alBufferData(buffer, format, samples, sampling)
        return ALSampledAudio(this, buffer)
    }

    override fun createSampler(samples: ShortBuffer, sampling: Int, stereo: Boolean): Audio {
        val buffer = alGenBuffers()
        val format = if (stereo) AL_FORMAT_STEREO16 else AL_FORMAT_MONO16
        alBufferData(buffer, format, samples, sampling)
        return ALSampledAudio(this, buffer)
    }

    override fun createSampler(samples: FloatBuffer, sampling: Int, stereo: Boolean) =
            if (alCaps.AL_EXT_FLOAT32) {
                val buffer = alGenBuffers()
                val format = if (stereo) AL_FORMAT_STEREO_FLOAT32 else AL_FORMAT_MONO_FLOAT32
                alBufferData(buffer, format, samples, sampling)
                ALSampledAudio(this, buffer)
            } else {
                //TODO fallback to 16bit
                DummyAudio
            }

    /**
     * Currently active audio streamers
     */
    private val streamers = mutableListOf<ALGenericStreamAudio>()

    /**
     * Create float 32bit streamer
     */
    override fun createStream(bufferSize: Int, sampling: Int, stereo: Boolean, sampler: FloatAudioDecoder): Audio =
            if (alCaps.AL_EXT_FLOAT32) {
                val stream = ALFloatStreamAudio(this, bufferSize, sampling, stereo, sampler)
                addStream(stream)
            } else {
                DummyAudio
            }

    /**
     * Create float 16bit streamer
     */
    override fun createStream(bufferSize: Int, sampling: Int, stereo: Boolean, sampler: ShortAudioDecoder): Audio {
        val stream = ALShortStreamAudio(this, bufferSize, sampling, stereo, sampler)
        addStream(stream)
        return stream
    }

    /**
     * Create float 8bit streamer
     */
    override fun createStream(bufferSize: Int, sampling: Int, stereo: Boolean, sampler: ByteAudioDecoder): Audio {
        val stream = ALByteStreamAudio(this, bufferSize, sampling, stereo, sampler)
        addStream(stream)
        return stream
    }

    /**
     * Remove from the streaming pile
     */
    fun removeStream(stream: ALGenericStreamAudio) {
        streamers.remove(stream)
    }

    /**
     * Add a streamed sound
     */
    fun addStream(stream: ALGenericStreamAudio): ALGenericStreamAudio {
        streamers.add(stream)
        return stream
    }

    /**
     * Update streaming audio
     */
    fun updateStreaming() {
        streamers.forEach {
            it.updateStreaming()
        }
    }


}