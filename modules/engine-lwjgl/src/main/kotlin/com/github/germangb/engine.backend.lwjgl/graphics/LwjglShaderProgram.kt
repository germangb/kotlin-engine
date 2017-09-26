package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.ShaderProgram
import org.lwjgl.opengl.GL20.*

/**
 * OpenGL shader program
 */
class LwjglShaderProgram(val program: Int,
                         val vertex: Int,
                         val fragment: Int) : ShaderProgram {
    override fun destroy() {
        glDeleteShader(vertex)
        glDeleteShader(fragment)
        glDeleteProgram(program)
    }

    /** Uniform locations */
    val uniforms = mutableMapOf<String, Int>()
}
