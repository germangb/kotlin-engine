package com.github.germangb.engine.backend.lwjgl.assets

import com.github.germangb.engine.assets.AssetLoader
import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.backend.lwjgl.audio.ALAudioDevice
import com.github.germangb.engine.backend.lwjgl.audio.VorbisSTBAudioDecoder
import com.github.germangb.engine.backend.lwjgl.audio.VorbisSTBStreamAudio
import com.github.germangb.engine.backend.lwjgl.core.LWJGLContext
import com.github.germangb.engine.backend.lwjgl.core.stackMemory
import com.github.germangb.engine.backend.lwjgl.fonts.STBTTFont
import com.github.germangb.engine.fonts.Font
import com.github.germangb.engine.graphics.*
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.Assimp.*
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.stb.STBVorbis.*
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.jemalloc.JEmalloc.je_free
import org.lwjgl.system.jemalloc.JEmalloc.je_malloc
import java.io.FileInputStream

class LWJGLAssetLoader(val audio: ALAudioDevice, val backend: LWJGLContext) : AssetLoader {
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
                texture = backend.graphics.createTexture(data, width[0], height[0], format, min, mag)
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
        val ttfData = je_malloc(1024 * 1024)
        val pixels = je_malloc(512 * 512)
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

                val pack = backend.graphics.createTexture(pixels, 512, 512, TexelFormat.R8, TextureFilter.LINEAR, TextureFilter.LINEAR)
                font = STBTTFont(pack, chars)
            }
        } finally {
            pixels.clear()
            je_free(pixels)
            je_free(ttfData)
        }

        return font
    }

    /**
     * Load mesh
     */
    override fun loadMesh(path: String, usage: MeshUsage, attributes: Array<out VertexAttribute>): Mesh? {
        val flags = aiProcess_Triangulate or
                aiProcess_GenUVCoords or
                aiProcess_GenNormals or
                aiProcess_LimitBoneWeights or
                aiProcess_FlipUVs

        val scene = aiImportFile(path, flags) ?: return null
        val aimesh = AIMesh.create(scene.mMeshes()[0])
        val mesh = aiMeshToGL(aimesh, attributes, usage, backend.graphics)
        aiFreeScene(scene)
        return mesh
    }

    /**
     * Load stream of audio
     */
    override fun loadAudio(path: String, stream: Boolean): Audio? {
        var sound: Audio? = null

        stackMemory {
            // open file
            if (stream) {
                val error = mallocInt(1)

                val handle = stb_vorbis_open_filename(path, error, null)
                if (handle == NULL) {
                    // TODO error
                } else {
                    // decode info
                    val info = STBVorbisInfo.callocStack(this)
                    stb_vorbis_get_info(handle, info)

                    // create decoder & return sound
                    val decoder = VorbisSTBAudioDecoder(handle, info.channels())
                    val vorbisSound = VorbisSTBStreamAudio(audio, info.sample_rate(), info.channels() == 2, decoder)
                    sound = audio.REGISTER_AUDIO(vorbisSound)
                }
            } else {
                val channels = mallocInt(1)
                val sampling = mallocInt(1)
                val samples = stb_vorbis_decode_filename(path, channels, sampling)

                if (samples == null) {
                    //TODO error
                } else {
                    sound = audio.createAudio(samples, sampling[0], channels[0] == 2)
                }
            }
        }

        return sound
    }
}