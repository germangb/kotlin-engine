package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.graphics.Framebuffer
import com.github.germangb.engine.graphics.Texture
import org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0

/**
 * Lwjgl framebuffer
 */
class GLFramebuffer(val gfx: GLGraphicsDevice,
                    val id: Int,
                    override val width: Int,
                    override val height: Int,
                    override val targets: List<Texture>) : Framebuffer {
    init {
        try {
            gfx.ifbos.add(this)
        } catch (e: Exception) {
            // window fbo
        }
    }

    /**
     * Draw buffers
     */
    val drawBuffers = IntArray(targets.size, { GL_COLOR_ATTACHMENT0 + it })

    override fun destroy() {
        targets.forEach { it.destroy() }
        gfx.ifbos.remove(this)
    }
}
