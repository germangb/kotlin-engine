package com.github.germangb.engine.animation

import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Quaternion
import com.github.germangb.engine.math.Vector3

/**
 * Animation timeline
 */
class AnimationTimeline(private val rotation: Array<RotationKey>,
                        private val position: Array<PositionKey>,
                        private val scale: Array<ScaleKey>) {
    companion object {
        val auxVec = Vector3()
        val auxQuat = Quaternion()
    }
    /**
     * Applies transform bind a matrix given a normalized duration
     */
    fun applyTransform(time: Float, out: Matrix4, interpolate: Boolean) {
        val from = time.toInt() % rotation.size
        val to = (time.toInt() + 1) % rotation.size
        val alpha = time - from.toFloat()
        if (interpolate) {
            out.translate(position[from].key.lerp(position[to].key, alpha, auxVec))
            out.rotate(rotation[from].key.slerp(rotation[to].key, alpha, auxQuat))
        } else {
            out.translate(position[from].key)
            out.rotate(rotation[from].key)
        }
    }
}