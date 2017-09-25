package com.github.germangb.engine.backend.lwjgl

import com.github.germangb.engine.backend.lwjgl.graphics.loadTextureSTB
import com.github.germangb.engine.core.ResourceLoader
import com.github.germangb.engine.graphics.Graphics
import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.scene.Actor

class LwjglResourceLoader(val gfx: Graphics) : ResourceLoader {
    override fun loadTexture(path: String) = loadTextureSTB(gfx, path)

    override fun loadMesh(path: String): Mesh? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadActor(path: String): (Actor.() -> Unit)? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}