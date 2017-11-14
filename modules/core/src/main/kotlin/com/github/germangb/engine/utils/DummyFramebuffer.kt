package com.github.germangb.engine.utils

import com.github.germangb.engine.graphics.Framebuffer
import com.github.germangb.engine.graphics.FramebufferDimensions
import com.github.germangb.engine.graphics.Texture

object DummyFramebuffer : Framebuffer {
    override val dimensions = FramebufferDimensions(0, 0)
    override val textures = emptyList<Texture>()
    override fun destroy() = Unit
}