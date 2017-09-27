package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.Texture
import java.nio.ByteBuffer
import org.lwjgl.opengl.GL11.*

class GLTexture(val id: Int, override val width: Int, override val height: Int) : Texture {
    override fun destroy() {
        glDeleteTextures(id)
    }

    override fun setPixels(data: ByteBuffer) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPixels(data: ByteBuffer) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}