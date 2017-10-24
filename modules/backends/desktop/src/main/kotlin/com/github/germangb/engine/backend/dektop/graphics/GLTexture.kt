package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.graphics.Texture
import java.nio.ByteBuffer
import org.lwjgl.opengl.GL11.*

class GLTexture(val id: Int, override val width: Int, override val height: Int) : Texture {
    override fun destroy() {
        glDeleteTextures(id)
    }

    override fun setPixels(data: ByteBuffer, x: Int, y: Int, width: Int, height: Int) {
        glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, width, height, GL_RGBA, GL_UNSIGNED_BYTE, data)
    }

    override fun getPixels(data: ByteBuffer) {
        TODO("not implemented")
    }
}