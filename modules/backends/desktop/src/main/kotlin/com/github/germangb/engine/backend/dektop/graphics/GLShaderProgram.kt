package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.files.Files
import com.github.germangb.engine.graphics.ShaderProgram
import org.lwjgl.opengl.GL20.glDeleteProgram

/** Match includes */
val INCLUDE_REGEX = Regex("^\\s*#include\\s*\"(.)*\"\\s*$")

/** preprocess file includes */
fun CharSequence.inlineIncludes(files: Files): String = buildString {
    val lines = this@inlineIncludes.lines()

    lines.forEach {
        val trim = it.trim()
        if (INCLUDE_REGEX.matches(trim)) {
            val path = trim.substring(trim.indexOfFirst { it == '"' } + 1, trim.length - 1)

            // if the file exists, then continue loading
            files.getLocal(path).read()?.bufferedReader()?.let {
                val source = it.use { it.readText() }
                val inlined = source.inlineIncludes(files)
                append(inlined)
            } ?: let {
                // included a file that didn't exist
                throw Exception("Unable to include GLSL file ($path)")
            }
        } else {
            appendln(it)
        }
    }
}

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
