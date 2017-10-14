package com.github.germangb.engine.animation

/**
 * Implement animation manager
 */
class SimpleAnimationManager : AnimationManager {
    /** Managed animations */
    override val animations = mutableListOf<ManagedAnimation>()

    /**
     * Update animations
     */
    override fun update(step: Float) {
        animations.forEach { it.update(step) }
    }

    /**
     * Create a new
     */
    override fun createAnimation(control: AnimationController): Animation {
        val anim = ManagedAnimation(this, control)
        animations.add(anim)
        return anim
    }

    /**
     * Remove animation from manager
     */
    internal fun destroyAnimation(animation: ManagedAnimation) {
        animations.remove(animation)
    }
}

class ManagedAnimation(val manager: SimpleAnimationManager, override val controller: AnimationController): Animation {
    /** animation state */
    var istate = AnimationState.STOPPED

    fun update(step: Float) {
        if (istate == AnimationState.PLAYING) {
            controller.update(step)
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
        istate = AnimationState.STOPPED
        controller.reset()
    }

    override fun destroy() = manager.destroyAnimation(this)
}
