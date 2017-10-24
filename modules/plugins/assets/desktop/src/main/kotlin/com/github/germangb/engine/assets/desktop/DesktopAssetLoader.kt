package com.github.germangb.engine.assets.desktop

import com.github.germangb.engine.assets.AssetLoaderPlugin
import com.github.germangb.engine.audio.desktop.Audio
import com.github.germangb.engine.audio.desktop.VorbisSTBAudioDecoder
import com.github.germangb.engine.audio.desktop.VorbisSTBStreamAudio
import com.github.germangb.engine.backend.lwjgl.core.LWJGLContext
import com.github.germangb.engine.backend.lwjgl.core.stackMemory
import com.github.germangb.engine.graphics.*
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.Assimp.*
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.stb.STBVorbis.*
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryUtil.NULL

class DesktopAssetLoader(val backend: LWJGLContext) : AssetLoaderPlugin {
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
                    val vorbisSound = VorbisSTBStreamAudio(backend.audio, info.sample_rate(), info.channels() == 2, decoder)
                    sound = backend.audio.REGISTER_AUDIO(vorbisSound)
                    //sound = audio.createAudio(vorbisSound, info.sample_rate(), info.channels() == 2)
                }
            } else {
                val channels = mallocInt(1)
                val sampling = mallocInt(1)
                val samples = stb_vorbis_decode_filename(path, channels, sampling)

                if (samples == null) {
                    //TODO error
                } else {
                    sound = backend.audio.createAudio(samples, sampling[0], channels[0] == 2)
                }
            }
        }

        return sound
    }
}