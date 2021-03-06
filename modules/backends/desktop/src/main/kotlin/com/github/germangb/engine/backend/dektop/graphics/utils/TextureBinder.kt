package com.github.germangb.engine.backend.dektop.graphics.utils

import com.github.germangb.engine.backend.dektop.graphics.GLTexture
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*

val MAX_TEXTURE_UNITS = 16

/**
 * Keeps track of bound textures
 */
object TextureBinder {
    var count = 0
}

/**
 * Bind texture and return texture unit
 */
fun GLTexture.bind(): Int {
    val unit = TextureBinder.count
    glActiveTexture(GL_TEXTURE0+unit)
    glBindTexture(GL_TEXTURE_2D, id)
    TextureBinder.count++
    if(TextureBinder.count >= MAX_TEXTURE_UNITS) TextureBinder.count = 0
    return unit
}