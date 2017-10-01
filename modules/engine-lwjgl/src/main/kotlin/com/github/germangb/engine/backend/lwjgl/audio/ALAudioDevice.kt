package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.*
import com.github.germangb.engine.backend.lwjgl.audio.utils.DummyAudio
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
import org.lwjgl.system.jemalloc.JEmalloc
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import java.util.*

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

//        System.err.println("AL_VENDOR=${alGetString(AL_VENDOR)}")
//        System.err.println("AL_VERSION=${alGetString(AL_VERSION)}")
//        System.err.println("AL_RENDERER=${alGetString(AL_RENDERER)}")
//        System.err.println("AL_EXTENSIONS=${alGetString(AL_EXTENSIONS)}")

        // rewind error
        alGetError()
    }

    override fun destroy() {
        floatBuffer.clear()
        shortBuffer.clear()
        byteBuffer.clear()
        JEmalloc.je_free(floatBuffer)
        JEmalloc.je_free(shortBuffer)
        JEmalloc.je_free(byteBuffer)
        alDeleteBuffers(buffers)
        alcDestroyContext(ctx)
        alcCloseDevice(device)
    }

    override fun createAudio(samples: ByteBuffer, sampling: Int, stereo: Boolean): Audio {
        val buffer = alGenBuffers()
        val format = if (stereo) AL_FORMAT_STEREO8 else AL_FORMAT_MONO8
        alBufferData(buffer, format, samples, sampling)
        return ALSampledAudio(this, buffer)
    }

    override fun createAudio(samples: ShortBuffer, sampling: Int, stereo: Boolean): Audio {
        val buffer = alGenBuffers()
        val format = if (stereo) AL_FORMAT_STEREO16 else AL_FORMAT_MONO16
        alBufferData(buffer, format, samples, sampling)
        return ALSampledAudio(this, buffer)
    }

    override fun createAudio(samples: FloatBuffer, sampling: Int, stereo: Boolean) =
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
     * Buffer pool
     */
    val bufferQueue = LinkedList<Int>()

    /** Pre allocated al buffers */
    private val buffers = IntArray(128)

    val floatBuffer = JEmalloc.je_malloc(16_000_00*4).asFloatBuffer()
    val shortBuffer = JEmalloc.je_malloc(16_000_00*2).asShortBuffer()
    val byteBuffer = JEmalloc.je_malloc(16_000_00)

    init {
        alGenBuffers(buffers)
        buffers.forEach {
            bufferQueue.add(it)
        }
    }

    /**
     * Create float 32bit streamer
     */
    override fun createAudio(sampler: FloatAudioDecoder, bufferSize: Int, sampling: Int, stereo: Boolean): Audio =
            if (alCaps.AL_EXT_FLOAT32) {
                val stream = ALFloatStreamAudio(this, bufferSize, sampling, stereo, sampler)
                addStream(stream)
            } else {
                DummyAudio
            }

    /**
     * Create float 16bit streamer
     */
    override fun createAudio(sampler: ShortAudioDecoder, bufferSize: Int, sampling: Int, stereo: Boolean): Audio {
        val stream = ALShortStreamAudio(this, bufferSize, sampling, stereo, sampler)
        addStream(stream)
        return stream
    }

    /**
     * Create float 8bit streamer
     */
    override fun createAudio(sampler: ByteAudioDecoder, bufferSize: Int, sampling: Int, stereo: Boolean): Audio {
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