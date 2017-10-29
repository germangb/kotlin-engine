package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.backend.dektop.core.stackMemory
import com.github.germangb.engine.backend.dektop.graphics.utils.bind
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.math.*
import org.lwjgl.opengl.GL20.*
import java.nio.FloatBuffer

class GLUniforms {
    lateinit var program: GLShaderProgram

    fun setup(prog: GLShaderProgram) {
        program = prog
    }

    fun getUniform(name: String): Int {
        program.uniforms[name]?.let {
            return it
        } ?: let {
            val loc = glGetUniformLocation(program.program, name)
            program.uniforms[name] = loc
            return loc
        }
    }

    fun uniform(name: String, value: Int) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform1i(unif, value)
    }

    fun uniform(name: String, value: Float) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform1f(unif, value)
    }

    fun uniform(name: String, value: Texture) {
        if (value is GLTexture) {
            val unif = getUniform(name)
            if (unif >= 0) glUniform1i(unif, value.bind())
        }
    }

    fun uniform(name: String, value: Vector2c) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform2f(unif, value.x(), value.y())
    }

    fun uniform(name: String, value: Vector2ic) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform2i(unif, value.x(), value.y())
    }

    fun uniform(name: String, value: Vector3c) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform3f(unif, value.x(), value.y(), value.z())
    }

    fun uniform(name: String, value: Vector3ic) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform3i(unif, value.x(), value.y(), value.z())
    }

    fun uniform(name: String, value: Vector4c) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform4f(unif, value.x(), value.y(), value.z(), value.w())
    }

    fun uniform(name: String, value: Vector4ic) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform4i(unif, value.x(), value.y(), value.z(), value.w())
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

    fun uniform(name: String, value: Matrix3c) {
        val unif = getUniform(name)
        if (unif >= 0) {
            stackMemory {
                val data = value.get(mallocFloat(9))
                glUniformMatrix4fv(unif, false, data)
            }
        }
    }

    fun uniform(name: String, value: Matrix4Buffer) {
        val unif = getUniform(name)
        glUniformMatrix4fv(unif, false, value.container)
    }

    fun uniform(name: String, value: Matrix3Buffer) {
        val unif = getUniform(name)
        glUniformMatrix4fv(unif, false, value.container)
    }
}