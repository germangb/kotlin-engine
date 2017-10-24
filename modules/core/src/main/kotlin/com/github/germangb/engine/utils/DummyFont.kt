package com.github.germangb.engine.utils

import com.github.germangb.engine.fonts.Font

object DummyFont : Font {
    override val texture = DummyTexture
    override fun destroy() = Unit
}