package com.github.germangb.engine.backend.dektop.audio

import com.github.germangb.engine.audio.AudioState
import org.lwjgl.openal.AL10.*

/**
 * AudioDevice inside of a buffer
 */
class ALSampledAudio(audio: ALAudioDevice, private val buffer: Int) : ALAudio(audio) {
    init {
        alSourcei(source, AL_BUFFER, buffer)
    }

    /** Audio state property */
    override val state: AudioState
        get() = when {
            destroyed -> AudioState.STOPPED
            alGetSourcei(source, AL_SOURCE_STATE) == AL_PLAYING -> AudioState.PLAYING
            alGetSourcei(source, AL_SOURCE_STATE) == AL_STOPPED -> AudioState.PAUSED
            else -> AudioState.STOPPED
        }

    /**
     * Has this audio been destroyed?
     */
    private var destroyed = false

    /**
     * Play sampled audio
     */
    override fun play(loop: Boolean) {
        alSourcei(source, AL_LOOPING, if (loop) AL_TRUE else AL_FALSE)
        alSourcePlay(source)
    }

    override fun pause() {
        alSourcePause(source)
    }

    override fun stop() {
        alSourceStop(source)
    }

    override fun destroy() {
        if (!destroyed) {
            super.destroy()
            alDeleteSources(source)
            alDeleteBuffers(buffer)
            destroyed = true
        }
    }

}