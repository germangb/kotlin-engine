package com.github.germangb.engine.animation

/**
 * Animation channel
 */
interface Animation {
    /**
     * Play animation
     */
    fun play()

    /**
     * Pause animation
     */
    fun pause()

    /**
     * Stop animation
     */
    fun stop()
}