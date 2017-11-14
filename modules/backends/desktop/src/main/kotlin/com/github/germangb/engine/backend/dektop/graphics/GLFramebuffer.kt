package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.graphics.Framebuffer
import com.github.germangb.engine.graphics.FramebufferDimensions
import com.github.germangb.engine.graphics.Texture
import org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0

/**
 * Lwjgl framebuffer
 */
class GLFramebuffer(val gfx: GLGraphicsDevice,
                    val id: Int,
                    width: Int,
                    height: Int,
                    override val textures: List<Texture>) : Framebuffer {
    init {
        try {
            gfx.ifbos.add(this)
        } catch (e: Exception) {
            // window fbo
        }
    }

    override val dimensions = FramebufferDimensions(width, height)

    /** Draw buffers */
    val drawBuffers = IntArray(textures.size, { GL_COLOR_ATTACHMENT0 + it })

    override fun destroy() {
        textures.forEach { it.destroy() }
        gfx.ifbos.remove(this)
    }
}
