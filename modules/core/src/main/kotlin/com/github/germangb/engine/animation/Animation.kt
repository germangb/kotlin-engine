package com.github.germangb.engine.animation

import com.github.germangb.engine.utils.Destroyable

/**
 * Animation listener
 */
interface AnimationListener {
    /**
     * Called when animation starts
     */
    fun onPlay(animation: Animation<*>) = Unit

    /**
     * Called when animation is paused
     */
    fun onPause(animation: Animation<*>) = Unit

    /**
     * Called when animation stops
     */
    fun onStop(animation: Animation<*>) = Unit

    /**
     * Called when animation ends
     */
    fun onEnd(animation: Animation<*>) = Unit

    /**
     * Called when animation loops
     */
    fun onLoop(animation: Animation<*>) = Unit
}

/**
 * Animation channel
 */
interface Animation<out T : AnimationController> : Destroyable {
    /**
     * Animation state
     */
    val state: AnimationState

    /**
     * Animation controller of this animation
     */
    val controller: T

    /**
     * Animation listener
     */
    var listener: AnimationListener?

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
