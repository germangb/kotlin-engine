package com.github.germangb.engine.animation

interface AnimationController {
    /**
     * How long this animation lasts
     */
    val duration: Float

    /**
     * Seek animation
     */
    fun seek(time: Float)
}