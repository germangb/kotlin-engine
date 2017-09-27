package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.Sound
import com.github.germangb.engine.audio.SoundState

/**
 * An audio thing that does NOTHING
 */
object DummyAudio : Sound {
    override val state = SoundState.STOPPED
    override fun play(loop: Boolean) = Unit
    override fun pause() = Unit
    override fun stop() = Unit
    override fun destroy() = Unit
}