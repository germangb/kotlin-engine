package com.github.germangb.engine.assets.utils

import com.github.germangb.engine.graphics.Framebuffer
import com.github.germangb.engine.graphics.Texture

object DummyFramebuffer : Framebuffer {
    override val width = 0
    override val height = 0
    override val targets = emptyList<Texture>()
}