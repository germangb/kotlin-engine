package com.github.germangb.engine.animation

interface AnimationManager {
    /**
     * Active animations
     */
    val animations: List<Animation<*>>

    /**
     * Create and manage animation
     */
    fun <T: AnimationController> createAnimation(control: T): Animation<T>

    /**
     * Set animations
     */
    fun update(step: Float)
}