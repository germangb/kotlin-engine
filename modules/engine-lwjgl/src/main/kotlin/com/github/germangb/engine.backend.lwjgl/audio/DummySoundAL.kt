package com.github.germangb.engine.backend.lwjgl.audio

import com.github.germangb.engine.audio.Sound

/**
 * An audio thing that does NOTHING
 */
object DummySoundAL : Sound {
    override fun play(loop: Boolean) = Unit
    override fun pause() = Unit
    override fun stop() = Unit
    override fun destroy() = Unit
}