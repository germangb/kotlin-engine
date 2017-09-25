package com.github.germangb.engine.scene

/**
 * Contains a hierarchical representation of the scene and a camera
 */
class Scene {
    /**
     * List of actors in the scene
     */
    internal val iactors = mutableListOf<Actor>()

    /**
     * Root node of the scene
     */
    val root = Actor(this, null)

    /**
     * Scene camera
     */
    val camera = Camera()

    /**
     * Update actors
     */
    fun update() {
        root.update()
        root.updateTransforms()
    }
}