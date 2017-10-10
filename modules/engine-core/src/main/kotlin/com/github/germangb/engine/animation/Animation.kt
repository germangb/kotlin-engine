package com.github.germangb.engine.animation

import com.github.germangb.engine.core.Destroyable

/**
 * Animation channel
 */
interface Animation: Destroyable {
    /** Animation state */
    val state: AnimationState

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