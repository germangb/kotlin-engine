package com.github.germangb.engine.animation

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Transform

/**
 * This assumes the timelines are sorted by time
 */
class SampledAnimationController(private val root: Actor,
                                 private val frames: Int,
                                 private val fps: Int,
                                 private val timeline: Map<String, AnimationTimeline>,
                                 private val interpolate: Boolean = true) : AnimationController {
    /**
     * Cached bone names
     */
    private val bones = mutableMapOf<String, Transform>()

    /**
     * Animation duration
     */
    override val duration = frames / fps.toFloat()

    init {
        timeline.forEach { node, _ ->
            root.find(node)?.let {
                bones[node] = it.transform
            }
        }
    }

    /**
     * Update actor transformations
     */
    private fun updateTransforms(frameTime: Float) {
        val modFrameTime = minOf(frameTime, frames.toFloat())
        timeline.forEach { node, timeline ->
            bones[node]?.let {
                val timeCompute = maxOf(minOf(modFrameTime, frames.toFloat()), 0f)
                timeline.applyTransform(timeCompute, it.local.identity(), interpolate)
            }
        }
    }

    /** Seek animation timeline */
    override fun seek(time: Float) {
        updateTransforms(time * fps)
    }
}