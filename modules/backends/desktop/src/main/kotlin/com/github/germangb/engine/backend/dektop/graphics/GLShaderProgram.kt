package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.files.Files
import com.github.germangb.engine.files.readText
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
            val included = files.getLocal(path)
            included.inputStream?.let {
                val source = included.readText()
                val inlined = source.inlineIncludes(files)
                append(inlined)
            } ?: throw Exception("Unable to find GLSL source file ($path)")
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
