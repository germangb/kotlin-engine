package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.Framebuffer
import com.github.germangb.engine.graphics.Texture

/**
 * Lwjgl framebuffer
 */
class LwjglFramebuffer(val id: Int,
                       override val width: Int,
                       override val height: Int,
                       override val targets: List<Texture>) : Framebuffer
