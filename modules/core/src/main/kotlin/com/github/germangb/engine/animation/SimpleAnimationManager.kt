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
    var loops = false
    var timer = 0f
    var ilistener: AnimationListener? = null

    override val time get() = timer

    override var listener: AnimationListener?
        get() = ilistener
        set(value) { ilistener = value }

    fun update(step: Float) {
        if (istate == AnimationState.PLAYING) {
            timer += step
            controller.seek(timer)

            // animation ending condition
            if (timer > controller.duration) {
                if (loops) {
                    // rewing and continue playing
                    while (timer > controller.duration) timer -= controller.duration
                    controller.seek(timer)
                    listener?.onLoop(this)
                } else {
                    // stop animation
                    controller.seek(controller.duration)
                    istate = AnimationState.ENDED
                    listener?.onEnd(this)
                }
            }
        }
    }

    override val state get() = istate

    override fun play(loop: Boolean) {
        if (istate == AnimationState.ENDED) {
            timer = 0f
        }
        istate = AnimationState.PLAYING
        loops = loop
        listener?.onPlay(this)
    }

    override fun pause() {
        istate = AnimationState.PAUSED
        listener?.onPause(this)
    }

    override fun stop() {
        istate = AnimationState.STOPPED
        timer = 0f
        controller.seek(0f)
        listener?.onStop(this)
    }

    override fun destroy() = manager.destroyAnimation(this)
}
