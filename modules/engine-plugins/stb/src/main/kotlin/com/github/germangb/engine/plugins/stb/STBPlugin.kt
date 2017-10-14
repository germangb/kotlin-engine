package com.github.germangb.engine.plugins.stb

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import java.nio.ByteBuffer

/**
 * Plugin stuff
 */
interface STBPlugin : STB, Plugin

/**
 * STB interface
 */
interface STB {
    /**
     * Call said function from STB library...
     */
    fun stb_easy_font_print(x: Float, y: Float, text: String, vertexData: ByteBuffer): Int
}

/**
 * Get STB plugin
 */
val Context.stb get() = getPlugin(STBPlugin::class) as? STB