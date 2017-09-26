package com.github.germangb.engine.audio

import com.github.germangb.engine.core.Destroyable

interface Sound : Destroyable {
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