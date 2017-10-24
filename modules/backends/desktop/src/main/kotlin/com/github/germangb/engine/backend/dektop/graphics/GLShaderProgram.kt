package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.graphics.ShaderProgram
import org.lwjgl.opengl.GL20.*

/**
 * OpenGL shader program
 */
class GLShaderProgram(val program: Int) : ShaderProgram {

    override fun destroy() {
        glDeleteProgram(program)
    }

    /** Uniform locations */
    val uniforms = mutableMapOf<String, Int>()
}
