package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.backend.dektop.core.stackMemory
import com.github.germangb.engine.backend.dektop.graphics.utils.bind
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.math.*
import org.lwjgl.opengl.GL20.*
import java.nio.FloatBuffer

class GLUniforms(val program: GLShaderProgram<*>, val uniformData: FloatBuffer) {
    /**
     * Get uniform location
     */
    fun getUniform(name: String): Int {
        program.uniforms[name]?.let {
            return it
        } ?: let {
            val loc = glGetUniformLocation(program.program, name)
            program.uniforms[name] = loc
            return loc
        }
    }

    fun Int.bind(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform1i(unif, this)
    }

    fun Float.bind(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform1f(unif, this)
    }

    fun uniform(name: String, value: Texture) {
        if (value is GLTexture) {
            val unif = getUniform(name)
            if (unif >= 0) glUniform1i(unif, value.bind())
        }
    }

    fun Vector2c.bind(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform2f(unif, x(), y())
    }

    fun Vector3c.bind(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform3f(unif, x(), y(), z())
    }

    fun Vector4c.bind(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform4f(unif, x(), y(), z(), w())
    }

    fun uniform(name: String, value: Matrix4c) {
        val unif = getUniform(name)
        if (unif >= 0) {
            stackMemory {
                val data = value.get(mallocFloat(16))
                glUniformMatrix4fv(unif, false, data)
            }
        }
    }

    fun uniform(name: String, value: Array<Matrix4c>) {
        val unif = getUniform(name)
        if (unif >= 0) {
            uniformData.clear()
            value.forEachIndexed { index, matrix4fc ->
                matrix4fc.get(uniformData).position(16 * (index + 1))
            }
            uniformData.flip()
            glUniformMatrix4fv(unif, false, uniformData)
        }
    }
}