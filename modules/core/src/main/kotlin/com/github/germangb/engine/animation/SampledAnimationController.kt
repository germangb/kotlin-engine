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
    private var frameTime = 0f

    override val duration = frames.toFloat() / fps
    override val time get() = frameTime / fps

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

    private fun advance(step: Float) {
        frameTime += step * fps
    }

    private fun updateTransforms() {
        val modFrameTime = frameTime % frames
        timeline.forEach { node, timeline ->
            bones[node]?.let {
                val timeCompute = maxOf(minOf(modFrameTime, frames.toFloat()), 0f)
                timeline.applyTransform(timeCompute, it.local.identity(), interpolate)
            }
        }
    }

    override fun seek(time: Float) {
        this.frameTime = time * fps
        updateTransforms()
    }
}