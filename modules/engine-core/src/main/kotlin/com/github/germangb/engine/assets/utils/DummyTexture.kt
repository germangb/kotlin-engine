package com.github.germangb.engine.assets.utils

import com.github.germangb.engine.graphics.Texture
import java.nio.ByteBuffer

object DummyTexture : Texture {
    override val width = 0
    override val height = 0
    override fun setPixels(data: ByteBuffer, x: Int, y: Int, width: Int, height: Int) = Unit
    override fun getPixels(data: ByteBuffer) = Unit
    override fun destroy() = Unit
}