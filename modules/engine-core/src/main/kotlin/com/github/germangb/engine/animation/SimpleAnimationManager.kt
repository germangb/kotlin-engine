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
    var loop = false
    var time = 0f

    var ilistener: AnimationListener? = null

    override var listener: AnimationListener?
        get() = ilistener
        set(value) { ilistener = value }

    fun update(step: Float) {
        if (istate == AnimationState.PLAYING) {
            controller.update(step)
            time += step

            if (time >= controller.duration) {
                if (loop) {
                    while (time > controller.duration) time -= controller.duration
                    controller.seek(time)
                    listener?.onStop(this)
                    listener?.onLoop(this)
                } else {
                    istate = AnimationState.STOPPED
                    time = controller.duration
                    controller.seek(time)
                    listener?.onStop(this)
                }
            }
        }
    }

    override val state get() = istate

    override fun play(loop: Boolean) {
        this.loop = loop
        istate = AnimationState.PLAYING
        listener?.onPlay(this)
    }

    override fun pause() {
        istate = AnimationState.PAUSED
        listener?.onPause(this)
    }

    override fun stop() {
        time = 0f
        istate = AnimationState.STOPPED
        controller.reset()
        listener?.onStop(this)
    }

    override fun destroy() = manager.destroyAnimation(this)
}
