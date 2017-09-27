package com.github.germangb.engine.backend.lwjgl.graphics.utils

import com.github.germangb.engine.backend.lwjgl.graphics.GLTexture
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*

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
    //glActiveTexture(GL_TEXTURE0)
    TextureBinder.count++
    if(TextureBinder.count >= 16) TextureBinder.count = 0
    return unit
}