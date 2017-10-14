package com.github.germangb.engine.plugins.stb.lwjgl

import com.github.germangb.engine.plugins.stb.STBPlugin
import org.lwjgl.stb.STBEasyFont.nstb_easy_font_print
import org.lwjgl.system.MemoryStack.stackGet
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.MemoryUtil.memAddress
import java.nio.ByteBuffer

object DesktopSTBPlugin : STBPlugin {
    /**
     * Do JNI call directly
     */
    override fun stb_easy_font_print(x: Float, y: Float, text: String, vertexData: ByteBuffer): Int {
        val stack = stackGet()
        val stackPointer = stack.pointer
        val textb = stackGet().ASCII(text)
        val call = nstb_easy_font_print(x, y, memAddress(textb), NULL, memAddress(vertexData), vertexData.remaining())
        stack.pointer = stackPointer
        return call
    }
}