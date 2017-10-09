package com.github.germangb.engine.animation

import com.github.germangb.engine.math.Matrix4

/**
 * Animation timeline
 */
class AnimationTimeline(private val rotation: List<RotationKey>,
                        private val position: List<PositionKey>) {
    /**
     * Applies transform to a matrix given a normalized duration
     */
    fun applyTransform(time: Float, out: Matrix4) {
        out.translate(position[time.toInt()].key)
        out.rotate(rotation[time.toInt()].key)
    }
}