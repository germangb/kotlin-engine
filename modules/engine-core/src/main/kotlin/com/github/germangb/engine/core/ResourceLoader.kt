package com.github.germangb.engine.core

import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.scene.Actor

interface ResourceLoader {
    /**
     * Load a texture
     */
    fun loadTexture(path: String): Texture?

    /**
     * Load a mesh
     */
    fun loadMesh(path: String): Mesh?

    /**
     * Load an acor blueprint
     */
    fun loadActor(path: String): (Actor.() -> Unit)?
}