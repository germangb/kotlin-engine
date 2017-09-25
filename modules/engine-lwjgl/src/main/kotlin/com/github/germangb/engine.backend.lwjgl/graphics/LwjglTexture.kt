package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.Texture
import java.nio.ByteBuffer

class LwjglTexture(val id: Int, override val width: Int, override val height: Int) : Texture {

    override fun setPixels(data: ByteBuffer) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPixels(data: ByteBuffer) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}