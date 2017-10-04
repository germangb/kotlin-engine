package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.Audio
import org.lwjgl.openal.AL10.*

/**
 * Registers a source in AL audio device
 */
abstract class ALAudio(val dev: ALAudioDevice) : Audio {

    val source = alGenSources()

    private var igain = 1f

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

    override fun destroy() {
        alDeleteSources(source)
        dev.alSources.remove(this)
    }
}