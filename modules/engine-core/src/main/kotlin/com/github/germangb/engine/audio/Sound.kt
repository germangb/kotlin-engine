package com.github.germangb.engine.audio

import com.github.germangb.engine.core.Destroyable

interface Sound : Destroyable {
    /**
     * Get sound state
     */
    val state: SoundState

    /**
     * Start sound playback
     */
    fun play(loop: Boolean = false)

    /**
     * Pause playback
     */
    fun pause()

    /**
     * Stop sound playback
     */
    fun stop()
}