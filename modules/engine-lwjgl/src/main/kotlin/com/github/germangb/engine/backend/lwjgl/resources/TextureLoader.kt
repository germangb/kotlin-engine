package com.github.germangb.engine.backend.lwjgl.resources

import com.github.germangb.engine.backend.lwjgl.graphics.LwjglTexture
import com.github.germangb.engine.backend.lwjgl.core.stackMemory
import com.github.germangb.engine.graphics.Graphics
import com.github.germangb.engine.graphics.Texture
import org.lwjgl.stb.STBImage.*
import org.lwjgl.opengl.GL11.*

/**
 * Load texture with STB library
 */
fun loadTextureSTB(gfx: Graphics, path: String): Texture? {
    var texture: Texture? = null

    stackMemory {
        val width = mallocInt(1)
        val height = mallocInt(1)
        val channels = mallocInt(1)
        val data = stbi_load(path, width, height, channels, 3)

        if (data == null) {
            System.err.println("${stbi_failure_reason()} ($path)")
        } else {
            val id = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, id)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, width[0], height[0], 0, GL_RGB, GL_UNSIGNED_BYTE, data)
            glBindTexture(GL_TEXTURE_2D, 0)

            texture = LwjglTexture(id, width[0], height[0])
        }
    }

    return texture
}