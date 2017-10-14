package com.github.germangb.engine.animation

import com.github.germangb.engine.core.Destroyable

/**
 * Animation channel
 */
interface Animation: Destroyable {
    /** Animation state */
    val state: AnimationState

    /**
     * Animation controller of this animation
     */
    val controller: AnimationController

    /**
     * Set animation listener
     */
    var listener: AnimationListener?

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

/**
 * Animation callbacks
 */
interface AnimationListener {
    /**
     * Called when animation is played
     */
    fun onPlay(animation: Animation)

    /**
     * Called when animation is paused
     */
    fun onPause(animation: Animation)

    /**
     * Called when animation is stopped
     */
    fun onStop(animation: Animation)

    /**
     * Called when animation is looped (normally after the onStop)
     */
    fun onLoop(animation: Animation)
}
