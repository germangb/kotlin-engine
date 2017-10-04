package com.github.germangb.engine.backend.lwjgl.assets

import com.github.germangb.engine.assets.AssetLoader
import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.backend.lwjgl.audio.ALAudioDevice
import com.github.germangb.engine.backend.lwjgl.audio.VorbisSTBAudioDecoder
import com.github.germangb.engine.backend.lwjgl.audio.VorbisSTBStreamAudio
import com.github.germangb.engine.backend.lwjgl.core.stackMemory
import com.github.germangb.engine.backend.lwjgl.fonts.STBTTFont
import com.github.germangb.engine.backend.lwjgl.graphics.GLGraphicsDevice
import com.github.germangb.engine.fonts.Font
import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.TexelFormat
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.graphics.TextureFilter
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.stb.STBVorbis.stb_vorbis_get_info
import org.lwjgl.stb.STBVorbis.stb_vorbis_open_filename
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.jemalloc.JEmalloc
import java.io.FileInputStream
import java.io.FileNotFoundException

class LWJGLAssetLoader(val audio: ALAudioDevice, val gfx: GLGraphicsDevice) : AssetLoader {
    /**
     * Load a generic resource
     */
    override fun loadGeneric(path: String) = try {
        FileInputStream(path)
    } catch (e: FileNotFoundException) {
        null
    }

    /**
     * Load texture file
     */
    override fun loadTexture(path: String, format: TexelFormat, min: TextureFilter, mag: TextureFilter): Texture? {
        var texture: Texture? = null

        stackMemory {
            val width = mallocInt(1)
            val height = mallocInt(1)
            val channels = mallocInt(1)
            val data = stbi_load(path, width, height, channels, format.channels)

            if (data == null) {
                System.err.println("${stbi_failure_reason()} ($path)")
            } else {
                texture = gfx.createTexture(data, width[0], height[0], format, min, mag)
            }
        }

        return texture
    }

    /**
     * Load a font
     */
    override fun loadFont(path: String, size: Int, charset: IntRange): Font? {
        var font: Font? = null

        // malloc memory
        val ttfData = JEmalloc.je_malloc(1024 * 1024)
        val pixels = JEmalloc.je_malloc(512 * 512)
        val chars: STBTTPackedchar.Buffer

        try {
            chars = STBTTPackedchar.malloc(charset.endInclusive - charset.start)

            FileInputStream(path).use {
                it.channel.read(ttfData)
                ttfData.flip()
            }

            stackMemory {
                // read font info
                //val info = STBTTFontinfo.malloc()
                //stbtt_InitFont(info, ttfData)

                val ctx = STBTTPackContext.mallocStack(this)
                stbtt_PackBegin(ctx, pixels, 512, 512, 0, 1)
                stbtt_PackFontRange(ctx, ttfData, 0, size.toFloat(), 0, chars)
                stbtt_PackEnd(ctx)

                val pack = gfx.createTexture(pixels, 512, 512, TexelFormat.R8, TextureFilter.LINEAR, TextureFilter.LINEAR)
                font = STBTTFont(pack, chars)
            }
        } finally {
            pixels.clear()
            JEmalloc.je_free(pixels)
            JEmalloc.je_free(ttfData)
        }

        return font
    }

    /**
     * Load mesh
     */
    override fun loadMesh(path: String): Mesh? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Load stream of audio
     */
    override fun loadAudio(path: String): Audio? {
        var sound: Audio? = null

        stackMemory {
            // open file
            val error = mallocInt(1)
            val handle = stb_vorbis_open_filename(path, error, null)

            if (handle != NULL) {
                // decode info
                val info = STBVorbisInfo.callocStack(this)
                stb_vorbis_get_info(handle, info)

//            System.err.println("file = $path")
//            System.err.println("#channels = ${info.channels()}")
//            System.err.println("#sampling = ${info.sample_rate()}")
//            System.err.println("#frame_size = ${info.max_frame_size()}")

                // create decoder & return sound
                val decoder = VorbisSTBAudioDecoder(handle, info.channels())
                val vorbisSound = VorbisSTBStreamAudio(audio, info.sample_rate(), info.channels() == 2, decoder)
                sound = audio.addStream(vorbisSound)
            }
        }

        return sound
    }
}