package com.github.germangb.engine.backend.lwjgl.audio.utils

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.audio.AudioState
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.math.Vector3c

/**
 * An audio thing that does NOTHING
 */
object DummyAudio : Audio {
    val vec = Vector3(0f)
    override val state = AudioState.STOPPED
    override fun play(loop: Boolean) = Unit
    override fun pause() = Unit
    override fun stop() = Unit
    override fun destroy() = Unit
    override var gain: Float = 0.0f
    override var velocity: Vector3c
        get() = vec
        set(value) = Unit
    override var position: Vector3c
        get() = vec
        set(value) = Unit
}