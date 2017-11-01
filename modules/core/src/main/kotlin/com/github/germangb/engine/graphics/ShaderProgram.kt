package com.github.germangb.engine.graphics

import com.github.germangb.engine.utils.Destroyable

/**
 * Shader program
 */
interface ShaderProgram : Destroyable {
    /**
     * Vertex shader source
     */
    val vertex: String

    /**
     * Fragment shader source
     */
    val fragment: String
}
