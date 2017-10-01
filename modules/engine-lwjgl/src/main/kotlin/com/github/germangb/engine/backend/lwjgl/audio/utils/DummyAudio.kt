package com.github.germangb.engine.backend.lwjgl.audio.utils

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.audio.AudioState

/**
 * An audio thing that does NOTHING
 */
object DummyAudio : Audio {
    override val state = AudioState.STOPPED
    override fun play(loop: Boolean) = Unit
    override fun pause() = Unit
    override fun stop() = Unit
    override fun destroy() = Unit
}