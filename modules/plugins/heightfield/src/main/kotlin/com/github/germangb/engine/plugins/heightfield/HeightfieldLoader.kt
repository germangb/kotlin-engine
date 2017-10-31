package com.github.germangb.engine.plugins.heightfield

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.utils.Destroyable
import java.nio.ShortBuffer

/**
 * Heightfield data. Buffer within it are allocated using ctx.buffers and therefore they must
 * be freed when they are no longer needed. Destroying this object will destroy all the associated assets
 * and memory contained on it.
 *
 * Just like in bullet physics, the world origin is assumed to be the center of the heightfield grid.
 */
class HeightfieldData(private val ctx: Context, val data: ShortBuffer, val channels: Int, val texture: Texture, val size: Int) : Destroyable {
    /**
     * Free data
     */
    override fun destroy() {
        data.clear()
        ctx.buffers.free(data)
        texture.destroy()
    }
}

/**
 * Contains utilities for loading a heightfield from a file
 */
interface HeightfieldLoader {
    /**
     * Load image as signed 16bit pixels. The results contains the ray 16bit data (it can be used with bullet to
     * create a heightfield shape) or it can be used to create a texture. This data is allocated using ctx.buffers
     * and therefore it must be freed when it is no longer needed (Keep in ming that bullet doesn't create a copy of
     * the buffer when you create a heightfield shape).
     */
    fun load16(file: FileHandle, desiredChannels: Int, createTexture: Boolean = true): HeightfieldData?
}