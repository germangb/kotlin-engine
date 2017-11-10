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
    override var fontSize = 15
    val string = StringBuilder()
    val rows = NVGTextRow.create(128)
    var show = false

    var debugHeight = 0f
    var offset = 0f

    override fun add(str: CharSequence) {
        string.append(str)
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

    override fun toggle() {
        show = show.not()
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
        val (width, height) = ctx.graphics.dimensions
        nvgBeginFrame(nv, width, height, 1f)

        // get paragraph
        val numRows = nvgTextBreakLines(nv, string, width.toFloat(), rows)
        string.setLength(0)

        // compute height
        val vel = ctx.time.delta * 512
        debugHeight = numRows * fontSize + 4f
        offset = if (show) {
            minOf(offset + vel, debugHeight)
        } else {
            maxOf(0f, offset - vel)
        }

        if (offset == 0f) return

        nvgResetTransform(nv)
        nvgTranslate(nv, 0f, offset - debugHeight)

        nvgBeginPath(nv)
        nvgFillColor(nv, color.rgba(0f, 0f, 0f, 0.4f))
        nvgRect(nv, 0f, 0f, width.toFloat(), numRows * fontSize.toFloat())
        nvgFill(nv)

        // pointer
        //println("$debugHeight $offset")
        if (ctx.input.mouse.insideWindow && show && offset == debugHeight) {
            val y = ((ctx.input.mouse.y + (debugHeight - offset)) / fontSize).toInt()
            if (y in 0..(numRows - 1)) {
                nvgBeginPath(nv)
                nvgFillColor(nv, color.rgba(0f, 0f, 0f, 0.15f))
                nvgRect(nv, 0f, fontSize * y.toFloat(), width.toFloat(), fontSize.toFloat())
                nvgFill(nv)
            }
        }

        nvgBeginPath(nv)
        nvgFillColor(nv, color.rgba(0f, 0f, 0f, 1f))
        nvgRect(nv, 0f, numRows * fontSize.toFloat(), width.toFloat(), 4f)
        nvgFill(nv)

        // draw text and shit
        nvgTextAlign(nv, NVG_ALIGN_LEFT or NVG_ALIGN_TOP)
        nvgFontFaceId(nv, font)
        nvgFontSize(nv, fontSize.toFloat())

        nvgFontBlur(nv, 0f)
        nvgFillColor(nv, color.rgba(0f, 0f, 0f, 1f))
        for (i in 0 until numRows) {
            val row = rows[i]
            nnvgText(nv, 0f + 1, fontSize * i + 1f, row.start(), row.end())
        }

        nvgFontBlur(nv, 4f)
        nvgFillColor(nv, color.rgba(0f, 0f, 0f, 1f))
        for (i in 0 until numRows) {
            val row = rows[i]
            nnvgText(nv, 0f, fontSize * i + 2f, row.start(), row.end())
        }

        nvgFontBlur(nv, 0f)
        nvgFillColor(nv, color.rgba(1f, 1f, 1f, 0.75f))
        for (i in 0 until numRows) {
            val row = rows[i]
            nnvgText(nv, 0f, fontSize * i.toFloat(), row.start(), row.end())
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