package com.github.germangb.engine.graphics

import java.nio.ByteBuffer

/**
 * Texture data
 */
interface Texture {
    /**
     * Texture width
     */
    val width: Int

    /**
     * Texture height
     */
    val height: Int

    /**
     * Set pixel data, read from the given buffer
     */
    fun setPixels(data: ByteBuffer)

    /**
     * Read pixel data and store in the passed buffer
     */
    fun getPixels(data: ByteBuffer)
}
