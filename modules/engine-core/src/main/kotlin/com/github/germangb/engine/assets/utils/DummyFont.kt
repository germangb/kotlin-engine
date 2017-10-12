package com.github.germangb.engine.assets.utils

import com.github.germangb.engine.assets.utils.DummyTexture
import com.github.germangb.engine.fonts.Font

object DummyFont : Font {
    override val texture = DummyTexture
    override fun destroy() = Unit
}