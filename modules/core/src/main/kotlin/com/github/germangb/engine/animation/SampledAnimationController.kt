package com.github.germangb.engine.animation

import com.github.germangb.engine.framework.Actor

/**
 * This assumes the timelines are sorted by time
 */
class SampledAnimationController(root: Actor,
                                 private val frames: Int,
                                 private val fps: Int,
                                 private val timeline: Map<String, AnimationTimeline>,
                                 private val interpolate: Boolean = true) : AnimationController {
    /**
     * Cached bone names
     */
    private val bones = mutableMapOf<String, Actor>()

    /**
     * Animation duration
     */
    override val duration = frames / fps.toFloat()

    init {
        val bfs = root.breadthFirstTraversal()
        bfs.forEach {
            if (it.name in timeline)
                bones[it.name] = it
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
                timeline.applyTransform(timeCompute, it.transform.identity(), interpolate)
            }
        }
    }

    /** Seek animation timeline */
    override fun seek(time: Float) = updateTransforms(time * fps)
}