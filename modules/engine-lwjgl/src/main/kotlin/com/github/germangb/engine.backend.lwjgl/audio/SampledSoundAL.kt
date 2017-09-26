package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.Sound
import org.lwjgl.openal.AL10.*

/**
 * Audio inside of a buffer
 */
class SampledSoundAL(private val audio: LwjglAudioAL, private val buffer: Int) : Sound {
    companion object {
        val DESTROYED_ERROR = "Sampled sound can't be used after destruction"
    }

    /**
     * Current audio source
     */
    private var currentSource = -1

    /**
     * Has this audio been destroyed?
     */
    private var destroyed = false

    /**
     * Play sampled audio
     */
    override fun play(loop: Boolean) {
        if (destroyed) throw Exception(DESTROYED_ERROR)
        audio.getAvailableSource(this)?.let {
            if (it != currentSource) {
                currentSource = it
                alSourcei(it, AL_BUFFER, buffer)
            }
            alSourcei(it, AL_LOOPING, if(loop) AL_TRUE else AL_FALSE)
            alSourcePlay(it)
        }
    }

    override fun pause() {
        if (destroyed) throw Exception(DESTROYED_ERROR)
        audio.getAvailableSource(this)?.let {
            if (it != currentSource) {
                currentSource = it
                alSourcei(it, AL_BUFFER, buffer)
            }
            alSourcePause(it)
        }
    }

    override fun stop() {
        if (destroyed) throw Exception(DESTROYED_ERROR)
        audio.getAvailableSource(this)?.let {
            if (it != currentSource) {
                currentSource = it
                alSourcei(it, AL_BUFFER, buffer)
            }
            alSourceStop(it)

            // free the source
            audio.addAvailableSource(this, it)
            currentSource = -1
        }
    }

    override fun destroy() {
        if (!destroyed) {
            alDeleteBuffers(buffer)
            if (currentSource != -1) {
                audio.addAvailableSource(this, currentSource)
                currentSource = -1
            }
            destroyed = true
        }
    }

}