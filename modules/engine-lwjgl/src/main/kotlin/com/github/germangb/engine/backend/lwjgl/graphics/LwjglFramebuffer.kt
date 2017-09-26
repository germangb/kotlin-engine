package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.Framebuffer
import com.github.germangb.engine.graphics.Texture
import org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0

/**
 * Lwjgl framebuffer
 */
class LwjglFramebuffer(val id: Int,
                       override val width: Int,
                       override val height: Int,
                       override val targets: List<Texture>) : Framebuffer {
    /**
     * Draw buffers
     */
    val drawBuffers = IntArray(targets.size, { GL_COLOR_ATTACHMENT0+it })

}
