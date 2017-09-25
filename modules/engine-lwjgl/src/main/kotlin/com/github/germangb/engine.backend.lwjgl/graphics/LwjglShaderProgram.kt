package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.ShaderProgram
import com.github.germangb.engine.graphics.VertexAttribute

/**
 * OpenGL shader program
 */
class LwjglShaderProgram(val program: Int,
                         val vertex: Int,
                         val fragment: Int,
                         override val attributes: List<VertexAttribute>) : ShaderProgram {

    /** Uniform locations */
    val uniforms = mutableMapOf<String, Int>()
}
