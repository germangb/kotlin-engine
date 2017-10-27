package com.github.germangb.engine.graphics

import com.github.germangb.engine.math.Matrix4

/**
 * Build an instancing instancing call
 */
interface Instancer {
    /**
     * Per-instance transformation
     */
    val transform: Matrix4

    /**
     * Add an instance to the instancing call
     */
    fun instance()

    /**
     * Flush draw batch
     */
    fun flush()

    /**
     * Upload uniforms
     */
    fun uniforms(action: Uniforms.() -> Unit)
}
