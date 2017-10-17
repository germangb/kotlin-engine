package com.github.germangb.engine.plugins.assimp

import com.github.germangb.engine.animation.AnimationTimeline
import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.framework.Actor

/**
 * Timeline for each (named) bone
 */
typealias AnimationTimelineData = Map<String, AnimationTimeline>

/**
 * Loaded animation data
 */
data class AnimationData(val frames: Int, val fps: Int, val timeline: AnimationTimelineData)


/**
 * Assimp API
 */
interface AssimpLoader {
    /**
     * Load scene blueprint using Assimp
     */
    fun loadActor(path: String, manager: AssetManager): (Actor.() -> Unit)?

    /**
     * Load animation data with Assimp
     */
    fun loadAnimations(path: String): AnimationData?
}