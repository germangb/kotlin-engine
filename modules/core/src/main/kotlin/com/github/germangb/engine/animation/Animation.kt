package com.github.germangb.engine.animation

import com.github.germangb.engine.core.Destroyable

/**
 * Animation channel
 */
interface Animation<out T: AnimationController>: Destroyable {
    /**
     * Animation state
     */
    val state: AnimationState

    /**
     * Animation controller of this animation
     */
    val controller: T

    /**
     * Animation time
     */
    val time: Float

    /**
     * Play animation
     */
    fun play(loop: Boolean = false)

    /**
     * Pause animation
     */
    fun pause()

    /**
     * Stop animation
     */
    fun stop()
}
