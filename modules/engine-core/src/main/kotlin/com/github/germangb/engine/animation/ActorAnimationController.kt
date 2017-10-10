package com.github.germangb.engine.animation

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Transform

class ActorAnimationController(val root: Actor, val duration: Float, val fps: Int, val timeline: Map<String, AnimationTimeline>, val interpolate: Boolean = true) : AnimationController {
    val bones = mutableMapOf<String, Transform>()
    var time = 0f

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
        time += step * fps
        if (time > duration) time = 0f
    }

    fun updateTransforms() {
        timeline.forEach { node, timeline ->
            bones[node]?.let {
                timeline.applyTransform(maxOf(minOf(time, duration), 0f), it.local.identity(), interpolate)
            }
        }
    }

    override fun seek(time: Float) {
        this.time = time * fps
        updateTransforms()
    }

    override fun reset() {
        time = 0f
        updateTransforms()
    }

}