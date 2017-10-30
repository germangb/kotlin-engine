package com.github.germangb.engine.plugins.debug.desktop

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.plugins.debug.DebugPlugin
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGTextRow
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.nvgCreate
import org.lwjgl.nanovg.NanoVGGL3.nvgDelete
import org.lwjgl.system.MemoryUtil.memAlloc
import org.lwjgl.system.MemoryUtil.memFree
import java.nio.ByteBuffer

class DesktopDebugPlugin(val ctx: Context) : DebugPlugin {
    /** NanoVG context pointer */
    var nv = 0L
    var font = 0
    lateinit var color: NVGColor
    var fontSize = 16f
    val string = StringBuilder()
    val rows = NVGTextRow.create(128)

    override fun add(build: StringBuilder.() -> Unit) = string.build()

    override fun add(str: CharSequence) {
        string.appendln(str)
    }

    override fun onPostInit() {
        // create context
        color = NVGColor.malloc()
        nv = nvgCreate(0)

        // load ttf font
        val fontData = resourceAsBuffer("NotoMono-Regular.ttf")
        font = nvgCreateFontMem(nv, "noto-mono", fontData, 0)

        memFree(fontData.clear())
    }

    fun resourceAsBuffer(path: String): ByteBuffer {
        val data = memAlloc(1000000)

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
        if (string.isEmpty()) return

        // begin drawing context
        val (width, height) = Pair(ctx.graphics.width, ctx.graphics.height)
        nvgBeginFrame(nv, width, height, 1f)

        // get paragraph
        val numRows = nvgTextBreakLines(nv, string, width.toFloat(), rows)
        string.setLength(0)

        // background
        nvgBeginPath(nv)
        nvgFillColor(nv, color.rgba(0f, 0f, 0f, 0.2f))
        nvgRect(nv, 0f, 0f, width.toFloat(), height.toFloat())
        nvgFill(nv)

        nvgBeginPath(nv)
        nvgFillColor(nv, color.rgba(0f, 0f, 0f, 0.25f))
        nvgRect(nv, 0f, 0f, width.toFloat(), numRows * fontSize)
        nvgFill(nv)

        nvgBeginPath(nv)
        nvgFillColor(nv, color.rgba(0f, 0f, 0f, 0.75f))
        nvgRect(nv, 0f, numRows * fontSize, width.toFloat(), 4f)
        nvgFill(nv)

        // draw text and shit
        nvgTextAlign(nv, NVG_ALIGN_LEFT or NVG_ALIGN_TOP)
        nvgFontFaceId(nv, font)
        nvgFontSize(nv, fontSize)

        nvgFontBlur(nv, 4f)
        nvgFillColor(nv, color.rgba(0f, 0f, 0f, 1f))
        for (i in 0 until numRows) {
            val row = rows[i]
            nnvgText(nv, 0f, fontSize * i + 2, row.start(), row.end())
        }

        nvgFontBlur(nv, 0f)
        nvgFillColor(nv, color.rgba(1f, 1f, 1f, 0.75f))
        for (i in 0 until numRows) {
            val row = rows[i]
            nnvgText(nv, 0f, fontSize * i, row.start(), row.end())
        }

        nvgEndFrame(nv)
    }

    fun NVGColor.rgba(r: Float, g: Float, b: Float, a: Float): NVGColor {
        r(r); g(g); b(b); a(a)
        return this
    }

    override fun onPostDestroy() {
        color.free()
        nvgDelete(nv)
    }
}