package com.github.engine.plugins.heightfield.desktop

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.graphics.TexelFormat
import com.github.germangb.engine.graphics.TextureFilter.LINEAR
import com.github.germangb.engine.plugins.heightfield.HeightfieldData
import com.github.germangb.engine.plugins.heightfield.HeightfieldPlugin
import com.github.germangb.engine.utils.DummyTexture
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load_16
import java.nio.ShortBuffer

/**
 * Desktop backend implementation
 */
class DesktopHeightfieldPlugin(val ctx: Context) : HeightfieldPlugin {
    /** Convert unsigned short to signed short */
    private fun Short.toSigned() = if (this <= Short.MAX_VALUE) (this + Short.MIN_VALUE) else (this - Short.MIN_VALUE)

    /**
     * Fast native implementation
     */
    override fun load16(file: FileHandle, desiredChannels: Int, createTexture: Boolean): HeightfieldData? {
        val x = IntArray(1)
        val y = IntArray(1)
        val c = IntArray(1)

        // load STB
        val data = stbi_load_16(file.path, x, y, c, desiredChannels) ?: return null

        // create texture
        val texture = if (createTexture) {
            val format = when (desiredChannels) {
                1 -> TexelFormat.R16F
                2 -> TexelFormat.RG16F
                else -> TexelFormat.RGB16F
            }

            // create texture
            ctx.graphics.createTexture(data, x[0], y[0], format, LINEAR, LINEAR)
        } else {
            DummyTexture
        }

        // allocate 16bit buffer
        val buffer = ctx.buffers.create(data.remaining() * 2L)
                .asShortBuffer()
                .put(data).flip() as ShortBuffer

        data.clear()
        stbi_image_free(data)

        // convert to signed values (so that buffer can be used with bullet)
        for (i in 0 until buffer.remaining()) buffer.put(i, buffer[i].toSigned().toShort())
        return HeightfieldData(buffer, desiredChannels, texture, x[0])
    }
}
