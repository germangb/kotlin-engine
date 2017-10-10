package com.github.germangb.engine.animation

interface AnimationController {
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