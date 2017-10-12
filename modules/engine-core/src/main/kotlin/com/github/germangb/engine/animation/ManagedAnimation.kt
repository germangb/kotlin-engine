package com.github.germangb.engine.animation

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