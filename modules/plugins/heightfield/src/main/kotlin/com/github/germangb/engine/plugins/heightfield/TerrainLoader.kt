package com.github.germangb.engine.plugins.heightfield

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.utils.Destroyable
import java.nio.*

/**
 * Heightfield data. Buffer within it are allocated using ctx.buffers and therefore they must
 * be freed when they are no longer needed. Destroying this object will destroy all the associated assets
 * and memory contained on it.
 *
 * Just like in bullet physics, the world origin is assumed to be the center of the terrain grid.
 */
class TerrainData<out T : Buffer>(private val ctx: Context, val data: T, val channels: Int, val texture: Texture, val size: Int) : Destroyable {
    /** Free data */
    override fun destroy() {
        texture.destroy()
        data.clear()
        when (data) {
            is ShortBuffer -> ctx.buffers.free(data)
            is ByteBuffer -> ctx.buffers.free(data)
            is IntBuffer -> ctx.buffers.free(data)
            is FloatBuffer -> ctx.buffers.free(data)
        }
    }
}

/**
 * Contains utilities for loading a terrain from a file
 */
interface TerrainLoader {
    companion object {
        val MODULE_NAME = "terrain_loader"
    }

    /** Load image as signed 16bit pixels */
    fun load16(file: FileHandle, desiredChannels: Int, createTexture: Boolean = true): TerrainData<ShortBuffer>?

    /** Load data as signed 8bit data */
    fun load8(file: FileHandle, desiredChannels: Int, createTexture: Boolean = true): TerrainData<ByteBuffer>?

    /** Load data as 32bit float */
    fun loadf(file: FileHandle, desiredChannels: Int, createTexture: Boolean = true): TerrainData<FloatBuffer>?
}