package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.graphics.ShaderProgram
import org.lwjgl.opengl.GL20.*

/**
 * OpenGL shader program
 */
class GLShaderProgram(val gfx: GLGraphicsDevice,
                      val program: Int,
                      override val vertex: String,
                      override val fragment: String) : ShaderProgram {
    init {
        gfx.ishaders.add(this)
    }
    /**
     * Destroy shader program
     */
    override fun destroy() {
        glDeleteProgram(program)
        gfx.ishaders.remove(this)
    }

    /** Uniform locations */
    val uniforms = mutableMapOf<String, Int>()
}
