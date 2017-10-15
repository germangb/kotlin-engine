package com.github.germangb.engine.animation

/**
 * Implement animation manager
 */
class SimpleAnimationManager : AnimationManager {
    /** Managed animations */
    private val ianimations = mutableListOf<ManagedAnimation<*>>()

    /**
     * List of active animations
     */
    override val animations: List<Animation<*>> get() = ianimations

    /**
     * Update animations
     */
    override fun update(step: Float) {
        ianimations.forEach { it.update(step) }
    }

    /**
     * Create a new
     */
    override fun <T: AnimationController> createAnimation(control: T): Animation<T> {
        val anim = ManagedAnimation(this, control)
        ianimations.add(anim)
        return anim
    }

    /**
     * Remove animation from manager
     */
    internal fun destroyAnimation(animation: ManagedAnimation<*>) {
        ianimations.remove(animation)
    }
}

class ManagedAnimation<out T: AnimationController>(val manager: SimpleAnimationManager, override val controller: T): Animation<T> {
    /** animation state */
    var istate = AnimationState.STOPPED
    var time = 0f

    fun update(step: Float) {
        if (istate == AnimationState.PLAYING) {
            controller.update(step)
            time += step
        }
    }

    override val state get() = istate

    override fun play() {
        istate = AnimationState.PLAYING
    }

    override fun pause() {
        istate = AnimationState.PAUSED
    }

    override fun stop() {
        time = 0f
        istate = AnimationState.STOPPED
        controller.seek(0f)
    }

    override fun destroy() = manager.destroyAnimation(this)
}
