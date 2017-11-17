package com.github.engine.plugins.heightfield.desktop

import com.github.germangb.engine.backend.dektop.core.DesktopModule
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.graphics.TexelFormat
import com.github.germangb.engine.graphics.TextureFilter.LINEAR
import com.github.germangb.engine.plugins.heightfield.TerrainData
import com.github.germangb.engine.plugins.heightfield.TerrainLoader
import com.github.germangb.engine.utils.DummyTexture
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load_16
import java.nio.ShortBuffer

/** Convert unsigned short to signed short */
private fun Short.toSigned() = if (this >= 0) this + Short.MIN_VALUE else this - Short.MIN_VALUE

/** Convert unsigned byte to signed byte */
private fun Byte.toSigned() = if (this >= 0) this + Byte.MIN_VALUE else this - Byte.MIN_VALUE

/**
 * Desktop backend implementation
 */
class DesktopHeightfieldPlugin(val ctx: Context) : DesktopModule, TerrainLoader {
    private val x = IntArray(1)
    private val y = IntArray(1)
    private val ch = IntArray(1)

    override fun load8(file: FileHandle, desiredChannels: Int, createTexture: Boolean) = TODO("not implemented")

    override fun loadf(file: FileHandle, desiredChannels: Int, createTexture: Boolean) = TODO("not implemented")

    override fun load16(file: FileHandle, desiredChannels: Int, createTexture: Boolean): TerrainData<ShortBuffer>? {
        val data = stbi_load_16(file.path, x, y, ch, desiredChannels) ?: return null
        val texture = if (createTexture) {
            ctx.graphics.createTexture(data, x[0], y[0], when (desiredChannels) {
                1 -> TexelFormat.R16
                2 -> TexelFormat.RG16
                else -> TexelFormat.RGB16
            }, LINEAR, LINEAR)
        } else DummyTexture

        // allocate 16bit buffer
        val buffer = ctx.buffers.create(data.remaining() * 2L)
                .asShortBuffer()
                .put(data).flip() as ShortBuffer

        data.clear()
        stbi_image_free(data)

        // convert to signed values (so that buffer can be used with bullet)
        for (i in 0 until buffer.remaining()) buffer.put(i, buffer[i].toSigned().toShort())
        return TerrainData(ctx, buffer, desiredChannels, texture, x[0])
    }
}
