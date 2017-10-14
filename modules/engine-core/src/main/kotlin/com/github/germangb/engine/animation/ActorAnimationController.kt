package com.github.germangb.engine.animation

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Transform

class ActorAnimationController(val root: Actor, val frames: Int, val fps: Int, val timeline: Map<String, AnimationTimeline>, val interpolate: Boolean = true) : AnimationController {
    val bones = mutableMapOf<String, Transform>()
    var itime = 0f

    override val duration = frames.toFloat() / fps

    override val time get() = itime

    init {
        timeline.forEach { node, _ ->
            root.find(node)?.let {
                bones[node] = it.transform
            }
        }
    }

    override fun update(step: Float) {
        advance(step)
        updateTransforms()
    }

    fun advance(step: Float) {
        itime += step
    }

    fun updateTransforms() {
        timeline.forEach { node, timeline ->
            bones[node]?.let {
                val timeCompute = maxOf(minOf(itime * fps, frames.toFloat()), 0f)
                timeline.applyTransform(timeCompute, it.local.identity(), interpolate)
            }
        }
    }

    override fun seek(time: Float) {
        this.itime = time
        updateTransforms()
    }

    override fun reset() {
        itime = 0f
        updateTransforms()
    }

}