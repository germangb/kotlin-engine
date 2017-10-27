package com.github.germangb.engine.utils

import com.github.germangb.engine.graphics.ShaderProgram

object SummyShaderProgram : ShaderProgram<Any> {
    override fun destroy() = Unit
}