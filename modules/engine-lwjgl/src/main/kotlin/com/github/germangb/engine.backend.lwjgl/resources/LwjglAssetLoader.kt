package com.github.germangb.engine.backend.lwjgl.resources

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.backend.lwjgl.graphics.LwjglGraphics
import com.github.germangb.engine.resources.AssetLoader
import com.github.germangb.engine.graphics.Mesh

class LwjglAssetLoader(private val gfx: LwjglGraphics) : AssetLoader {
    /**
     * Load texture file
     */
    override fun loadTexture(path: String) = loadTextureSTB(gfx, path)

    /**
     * Load mesh
     */
    override fun loadMesh(path: String): Mesh? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Load stream of audio
     */
    override fun loadSound(path: String): Audio? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}