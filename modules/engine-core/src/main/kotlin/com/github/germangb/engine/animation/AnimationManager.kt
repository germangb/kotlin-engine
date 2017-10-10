package com.github.germangb.engine.animation

interface AnimationManager {
    /**
     * Active animations
     */
    val animations: List<Animation>

    /**
     * Create and manage animation
     */
    fun createAnimation(control: AnimationController): Animation

    /**
     * Set animations
     */
    fun update(step: Float)
}