package com.github.germangb.engine.scene.components

import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.scene.Actor
import com.github.germangb.engine.scene.Component

/**
 * Contains mesh instancing information
 */
class MeshInstancerComponent(val mesh: Mesh, val texture: Texture?) : Component() {
    override fun init() = Unit
    override fun update() = Unit
}

/**
 * Adds a mesh instancer to the actor
 */
fun Actor.addMeshInstancer(mesh: Mesh, texture: Texture) {
    addComponent(MeshInstancerComponent(mesh, texture))
}
