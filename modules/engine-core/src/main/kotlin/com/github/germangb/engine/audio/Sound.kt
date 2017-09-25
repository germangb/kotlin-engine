package com.github.germangb.engine.audio

interface Sound {
    /**
     * Start sound playback
     */
    fun play()

    /**
     * Pause playback
     */
    fun pause()

    /**
     * Stop sound playback
     */
    fun stop()
}