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
import com.github.germangb.engine.graphics.*
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.Assimp.*
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.stb.STBVorbis.stb_vorbis_get_info
import org.lwjgl.stb.STBVorbis.stb_vorbis_open_filename
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.jemalloc.JEmalloc.je_free
import org.lwjgl.system.jemalloc.JEmalloc.je_malloc
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

                val pack = gfx.createTexture(pixels, 512, 512, TexelFormat.R8, TextureFilter.LINEAR, TextureFilter.LINEAR)
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
    override fun loadMesh(path: String, attributes: Set<VertexAttribute>): Mesh? {
        val flags = aiProcess_Triangulate or aiProcess_GenUVCoords or aiProcess_GenNormals or aiProcess_LimitBoneWeights
        val scene = aiImportFile(path, flags) ?: return null

        val mesh = AIMesh.create(scene.mMeshes()[0])

        // mesh attributes
        val positions = mesh.mVertices()
        val normals = mesh.mNormals()
        val uvs = mesh.mTextureCoords(0)

        // faces
        val faces = mesh.mFaces()

        // allocate data
        val vertexDataSize = 8 * positions.capacity() * 4L
        val indexDataSize = 3 * faces.capacity() * 4L
        val vertexData = je_malloc(vertexDataSize)
        val indexData = je_malloc(indexDataSize)

        // index data
        (0 until faces.capacity()).map { faces[it] }.forEach {
            val ind = it.mIndices()
            indexData.putInt(ind[0])
            indexData.putInt(ind[1])
            indexData.putInt(ind[2])
        }
        indexData.flip()

        fun VertexAttribute.addData(i: Int) {
            when(this) {
                VertexAttribute.POSITION -> {
                    vertexData.putFloat(positions[i].x())
                    vertexData.putFloat(positions[i].y())
                    vertexData.putFloat(positions[i].z())
                }
                VertexAttribute.NORMAL -> {
                    vertexData.putFloat(normals[i].x())
                    vertexData.putFloat(normals[i].y())
                    vertexData.putFloat(normals[i].z())
                }
                VertexAttribute.UV -> {
                    vertexData.putFloat(uvs[i].x())
                    vertexData.putFloat(uvs[i].y())
                }
                else -> {}
            }
        }

        // vertex data
        (0 until mesh.mNumVertices()).forEach {
            attributes.sorted().forEach { attr -> attr.addData(it) }
        }
        vertexData.flip()

        // create mesh
        val glMesh = gfx.createMesh(vertexData, indexData, MeshPrimitive.TRIANGLES, attributes, MeshUsage.STATIC)

        // free resources
        vertexData.clear()
        indexData.clear()
        je_free(vertexData)
        je_free(indexData)
        aiFreeScene(scene)
        return glMesh
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