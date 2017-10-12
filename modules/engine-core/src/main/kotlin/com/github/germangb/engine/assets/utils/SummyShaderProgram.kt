package com.github.germangb.engine.assets.utils

import com.github.germangb.engine.graphics.ShaderProgram

object SummyShaderProgram : ShaderProgram {
    override fun destroy() = Unit
}