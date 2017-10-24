package com.github.germangb.engine.backend.dektop.audio

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.math.Vector3c
import org.lwjgl.openal.AL10.*

/**
 * Registers a source in AL audio device
 */
abstract class ALAudio(val dev: ALAudioDevice) : Audio {
    /** OpenAL audio source */
    val source = alGenSources()

    private var igain = 1f
    private var iposition = Vector3(0f)
    private var ivelocity = Vector3(0f)

    override var gain: Float
        get() = igain
        set(value) {
            igain = value
            alSourcef(source, AL_GAIN, value * dev.gain)
        }


    init {
        // register al source
        dev.alSources.add(this)
    }

    override var position: Vector3c
        get() = iposition
        set(value) {
            alSource3f(source, AL_POSITION, value.x(), value.y(), value.z())
            iposition.set(value)
        }

    override var velocity: Vector3c
        get() = ivelocity
        set(value) {
            alSource3f(source, AL_VELOCITY, value.x(), value.y(), value.z())
            ivelocity.set(value)
        }

    override fun destroy() {
        alDeleteSources(source)
        dev.alSources.remove(this)
        dev.UNREGISTER_AUDIO(this)
    }
}