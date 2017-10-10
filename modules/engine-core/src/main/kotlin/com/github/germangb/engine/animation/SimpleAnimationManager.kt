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