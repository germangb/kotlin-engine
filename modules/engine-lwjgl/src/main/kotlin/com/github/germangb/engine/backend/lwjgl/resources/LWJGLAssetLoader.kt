package com.github.germangb.engine.backend.lwjgl.resources

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.backend.lwjgl.audio.ALAudioDevice
import com.github.germangb.engine.backend.lwjgl.audio.VorbisSTBAudioDecoder
import com.github.germangb.engine.backend.lwjgl.audio.VorbisSTBStreamAudio
import com.github.germangb.engine.backend.lwjgl.core.stackMemory
import com.github.germangb.engine.backend.lwjgl.graphics.GLGraphicsDevice
import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.TexelFormat
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.assets.AssetLoader
import com.github.germangb.engine.fonts.Font
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.stb.STBVorbis.stb_vorbis_get_info
import org.lwjgl.stb.STBVorbis.stb_vorbis_open_filename
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.stb.STBTruetype.*
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
    override fun loadTexture(path: String): Texture? {
        var texture: Texture? = null

        stackMemory {
            val width = mallocInt(1)
            val height = mallocInt(1)
            val channels = mallocInt(1)
            val data = stbi_load(path, width, height, channels, 3)

            if (data == null) {
                System.err.println("${stbi_failure_reason()} ($path)")
            } else {
                texture = gfx.createTexture(data, width[0], height[0], TexelFormat.RGB8)
            }
        }

        return texture
    }

    /**
     * Load a font
     */
    override fun loadFont(path: String): Font? {
        return null
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
    override fun loadAudio(path: String, forceMono: Boolean): Audio? {
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