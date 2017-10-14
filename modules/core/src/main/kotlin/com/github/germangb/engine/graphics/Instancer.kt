package com.github.germangb.engine.graphics

import com.github.germangb.engine.math.Matrix4

/**
 * Build an instancing render call
 */
interface Instancer {
    /**
     * Per-instance transformation
     */
    val transform: Matrix4

    /**
     * Add an instance to the render call
     */
    fun instance()

    /**
     * Upload uniforms
     */
    fun uniforms(action: Uniforms.() -> Unit)
}
