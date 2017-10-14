package com.github.germangb.engine.plugins.assimp

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.framework.Actor

/**
 * Assimp API
 */
interface AssimpLoader {
    /**
     * Load assimp scene blueprint
     */
    fun loadActor(path: String, manager: AssetManager): (Actor.() -> Unit)?
}