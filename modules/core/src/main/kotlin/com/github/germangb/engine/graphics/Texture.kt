package com.github.germangb.engine.graphics

import com.github.germangb.engine.utils.Destroyable
import java.nio.ByteBuffer

/**
 * Texture data
 */
interface Texture : Destroyable {
    /**
     * Texture width
     */
    val width: Int

    /**
     * Texture height
     */
    val height: Int

    /**
     * Set pixel data, asInput from the given buffer
     */
    fun setPixels(data: ByteBuffer, x: Int, y: Int, width: Int, height: Int)

    /**
     * Set pixel data, asInput from the given buffer
     */
    fun setPixels(data: ByteBuffer) = setPixels(data, 0, 0, width, height)

    /**
     * Read pixel data and store in the passed buffer
     */
    fun getPixels(data: ByteBuffer)
}
