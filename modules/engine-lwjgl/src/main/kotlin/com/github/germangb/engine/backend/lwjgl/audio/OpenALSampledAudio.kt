package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.audio.AudioState
import org.lwjgl.openal.AL10.*

/**
 * AudioDevice inside of a buffer
 */
class OpenALSampledAudio(private val audio: ALAudioDevice, private val buffer: Int) : Audio {
    companion object {
        val DESTROYED_ERROR = "Sampled sound can't be used after destruction"
    }

    /**
     * Audio state
     */
    private var istate = AudioState.STOPPED

    /** Audio state property */
    override val state get() = istate

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
            istate = AudioState.PLAYING
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
            istate = AudioState.PAUSED
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
            istate = AudioState.STOPPED
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
            istate = AudioState.STOPPED
        }
    }

}