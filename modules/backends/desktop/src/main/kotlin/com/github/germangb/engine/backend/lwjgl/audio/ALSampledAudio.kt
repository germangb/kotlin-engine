package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.AudioState
import com.github.germangb.engine.backend.lwjgl.core.ASSERT_CONDITION
import org.lwjgl.openal.AL10.*

/**
 * AudioDevice inside of a buffer
 */
class ALSampledAudio(private val audio: ALAudioDevice, private val buffer: Int) : ALAudio(audio) {
    companion object {
        val DESTROYED_ERROR = "Sampled sound can't be used after destruction"
    }

    init {
        alSourcei(source, AL_BUFFER, buffer)
    }

    /** Audio state property */
    override val state: AudioState
        get() = when {
            destroyed -> AudioState.STOPPED
            alGetSourcei(source, AL_PLAYING) == AL_TRUE -> AudioState.PLAYING
            alGetSourcei(source, AL_PAUSED) == AL_TRUE -> AudioState.PAUSED
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
        ASSERT_CONDITION(destroyed, DESTROYED_ERROR)
        alSourcei(source, AL_LOOPING, if (loop) AL_TRUE else AL_FALSE)
        alSourcePlay(source)
    }

    override fun pause() {
        ASSERT_CONDITION(destroyed, DESTROYED_ERROR)
        alSourcePause(source)
    }

    override fun stop() {
        ASSERT_CONDITION(destroyed, DESTROYED_ERROR)
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