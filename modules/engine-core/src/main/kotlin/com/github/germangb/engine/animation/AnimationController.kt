package com.github.germangb.engine.animation

interface AnimationController {
    /**
     * Animation duration in seconds
     */
    val duration: Float

    /**
     * Get animation itime (in seconds)
     */
    val time: Float

    /**
     * Up
     */
    fun update(step: Float)

    /**
     * Seek animation
     */
    fun seek(time: Float)

    /**
     * Reset animation
     */
    fun reset()
}