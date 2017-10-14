package com.github.germangb.engine.plugins.debug.desktop

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.plugins.debug.DebugPlugin
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.util.*

class DesktopDebugPlugin(val ctx: Context) : DebugPlugin {
    /** NanoVG context pointer */
    var nv = 0L
    var font = 0
    lateinit var color: NVGColor
    val lines = LinkedList<String>()

    override fun addString(x: Float, y: Float, text: String) {
        //TODO
    }

    override fun addString(text: String) {
        lines.add(text)
    }

    override fun onPostInit() {
        // create context
        color = NVGColor.malloc()
        nv = nvgCreate(NVG_ANTIALIAS)

        // load ttf font
        val fontData = resourceAsBuffer("NotoMono-Regular.ttf")
        font = nvgCreateFontMem(nv, "noto-mono", fontData, 0)

        MemoryUtil.memFree(fontData.clear())
    }

    fun resourceAsBuffer(path: String): ByteBuffer {
        val data = MemoryUtil.memAlloc(1000000)

        javaClass.getResourceAsStream("../../../../../../../$path")?.use {
            val buffer = ByteArray(512)

            while (it.available() > 0) {
                val read = it.read(buffer)
                data.put(buffer, 0, read)
            }
        }

        data.flip()
        return data
    }

    override fun onPostUpdate() {
        // begin drawing context
        val (width, height) = Pair(ctx.graphics.width, ctx.graphics.height)
        nvgBeginFrame(nv, width, height, 1f)

        // font params
        nvgFontFaceId(nv, font)
        nvgFontSize(nv, 16f)

        // background
        nvgBeginPath(nv)
        nvgFillColor(nv, color.rgba(0f, 0f, 0f, 0.25f))
        nvgRect(nv, 0f, 0f, width.toFloat(), 16f * lines.size)
        nvgFill(nv)

        // draw text and shit
        nvgTextAlign(nv, NVG_ALIGN_LEFT or NVG_ALIGN_TOP)

        nvgFontBlur(nv, 2f)
        nvgFillColor(nv, color.rgba(0f, 0f, 0f, 1f))
        lines.forEachIndexed { index, line ->
            nvgText(nv, 0f, 16f * index + 1, line)
        }

        nvgFontBlur(nv, 0f)
        nvgFillColor(nv, color.rgba(1f, 1f, 1f, 0.75f))
        lines.forEachIndexed { index, line ->
            nvgText(nv, 0f, 16f * index, line)
        }

        nvgEndFrame(nv)

        lines.clear()
    }

    fun NVGColor.rgba(r: Float, g: Float, b: Float, a: Float): NVGColor {
        r(r)
        g(g)
        b(b)
        a(a)
        return this
    }

    override fun onPostDestroy() {
        color.free()
        nvgDelete(nv)
    }
}