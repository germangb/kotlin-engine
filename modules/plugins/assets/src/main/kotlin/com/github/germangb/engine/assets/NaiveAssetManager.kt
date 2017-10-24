package com.github.germangb.engine.assets

import com.github.germangb.engine.audio.desktop.Audio
import com.github.germangb.engine.graphics.*

class NaiveAssetManager(private val loader: AssetLoader) : AssetManager {
    private val textures = mutableMapOf<String, Texture?>()
    private val meshes = mutableMapOf<String, Mesh?>()
    private val audios = mutableMapOf<String, Audio?>()

    override fun destroy() {
        textures.values.filterNotNull().forEach { it.destroy() }
        meshes.values.filterNotNull().forEach { it.destroy() }
        audios.values.filterNotNull().forEach { it.destroy() }
    }

    override fun isLoaded(path: String) = path in textures || path in meshes || path in audios

    /**
     * Debug method
     */
    fun dump() {
        textures.forEach { (path, asset) -> println("$path [$asset]") }
        meshes.forEach { (path, asset) -> println("$path [$asset]") }
        audios.forEach { (path, asset) -> println("$path [$asset]") }
    }

    override fun preloadTexture(path: String, format: TexelFormat, min: TextureFilter, mag: TextureFilter) {
        if (textures[path] == null) {
            textures[path] = loader.loadTexture(path, format, min, mag)
        }
    }

    override fun preloadMesh(path: String, usage: MeshUsage, vararg attributes: VertexAttribute) {
        if (meshes[path] == null) {
            meshes[path] = loader.loadMesh(path, usage, *attributes)
        }
    }

    override fun preloadAudio(path: String, stream: Boolean) {
        if (audios[path] == null) {
            audios[path] = loader.loadAudio(path, stream)
        }
    }

    override fun getTexture(path: String) = textures[path]

    override fun getMesh(path: String) = meshes[path]

    override fun getAudio(path: String) = audios[path] ?: let {
        val loaded = loader.loadAudio(path)
        loader.loadAudio(path)?.let { audios[path] = it }
        loaded
    }

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