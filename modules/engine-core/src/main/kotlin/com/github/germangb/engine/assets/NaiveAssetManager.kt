package com.github.germangb.engine.assets

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.graphics.*

class NaiveAssetManager(private val loader: AssetLoader) : AssetManager {
    private val textures = mutableMapOf<String, Texture>()
    private val meshes = mutableMapOf<String, Mesh>()
    private val audios = mutableMapOf<String, Audio>()

    override fun destroy() {
        textures.values.forEach { it.destroy() }
        meshes.values.forEach { it.destroy() }
        audios.values.forEach { it.destroy() }
    }

    /**
     * Debug method
     */
    fun dump() {
        textures.forEach { (path, asset) -> println("$path [$asset]") }
        meshes.forEach { (path, asset) -> println("$path [$asset]") }
        audios.forEach { (path, asset) -> println("$path [$asset]") }
    }

    override fun loadTexture(path: String, format: TexelFormat, min: TextureFilter, mag: TextureFilter) {
        if (textures[path] == null) {
            loader.loadTexture(path, format, min, mag)?.let { textures[path] = it }
        }
    }

    override fun loadMesh(path: String, attributes: Set<VertexAttribute>) {
        if (meshes[path] == null) {
            loader.loadMesh(path, attributes)?.let { meshes[path] = it }
        }
    }

    override fun loadAudio(path: String) {
        if (audios[path] == null) {
            loader.loadAudio(path)?.let { audios[path] = it }
        }
    }

    override fun getTexture(path: String) = textures[path]

    override fun getMesh(path: String) = meshes[path]

    override fun getAudio(path: String) = audios[path]

    override fun delegateTexture(texture: Texture, path: String) {
        textures[path] = texture
    }

    override fun delegateMesh(mesh: Mesh, path: String) {
        meshes[path] = mesh
    }

    override fun delegateAudio(audio: Audio, path: String) {
        audios[path] = audio
    }

}