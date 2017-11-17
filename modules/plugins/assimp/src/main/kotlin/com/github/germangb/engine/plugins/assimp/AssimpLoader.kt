package com.github.germangb.engine.plugins.assimp

import com.github.germangb.engine.animation.AnimationTimeline
import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.graphics.Mesh

/**
 * Loaded animation data
 */
data class AnimationData(val frames: Int, val fps: Int, val timeline: Map<String, AnimationTimeline>)

/**
 * Assimp scene data
 */
data class AssimpSceneData(val meshes: List<Mesh>, val animations: List<AnimationData>, val actor: Actor.() -> Unit)

/** Load meshes */
val MESHES = 0x1

/** Load animations */
val ANIMATIONS = 0x2

/** Load scene */
val SCENE = 0x3

/** Loads everything */
val ALL = MESHES or ANIMATIONS or SCENE

/**
 * Assimp API
 */
interface AssimpLoader {
    companion object {
        val MODULE_NAME = "assimp_loader"
    }

    /**
     * Load assimp scene
     */
    fun loadScene(file: FileHandle, manager: AssetManager, flags: Int = ALL): AssimpSceneData?
}