package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.backend.lwjgl.graphics.utils.bind
import com.github.germangb.engine.backend.lwjgl.core.stackMemory
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.graphics.Uniforms
import com.github.germangb.engine.math.*
import org.lwjgl.opengl.GL20.*
import java.nio.FloatBuffer

class GLUniforms(val program: GLShaderProgram, val uniformData: FloatBuffer) : Uniforms {
    /**
     * Get uniform location
     */
    fun getUniform(name: String): Int {
        program.uniforms[name]?.let {
            return it
        }?: let {
            val loc = glGetUniformLocation(program.program, name)
            program.uniforms[name] = loc
            return loc
        }
    }

    override fun Int.bindsTo(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform1i(unif, this)
    }

    override fun Float.bindsTo(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform1f(unif, this)
    }

    override fun Texture.bindsTo(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform1i(unif, (this as GLTexture).bind())
    }

    override fun Vector2c.bindsTo(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform2f(unif, x(), y())
    }

    override fun Vector3c.bindsTo(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform3f(unif, x(), y(), z())
    }

    override fun Vector4c.bindsTo(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) glUniform4f(unif, x(), y(), z(), w())
    }

    override fun Matrix3c.bindsTo(name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Matrix4c.bindsTo(name: String) {
        val unif = getUniform(name)
        if (unif >= 0) {
            stackMemory {
                val data = get(mallocFloat(16))
                glUniformMatrix4fv(unif, false, data)
            }
        }
    }

    override fun Array<Matrix4c>.bindsTo(name: String) {
        val unif = getUniform(name)
        if(unif >= 0) {
            uniformData.clear()
            forEachIndexed { index, matrix4fc ->
                matrix4fc.get(uniformData).position(16 * (index + 1))
            }
            uniformData.flip()
            glUniformMatrix4fv(unif, false, uniformData)
        }
    }

    override fun Array<Matrix3c>.bindsTo(name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Array<Vector4c>.bindsTo(name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Array<Vector3c>.bindsTo(name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Array<Vector2c>.bindsTo(name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}