package com.github.germangb.engine.audio

import com.github.germangb.engine.core.Destroyable

interface Audio : Destroyable {
    /**
     * Get sound state
     */
    val state: AudioState

    /**
     * audio gain property
     */
    var gain: Float

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