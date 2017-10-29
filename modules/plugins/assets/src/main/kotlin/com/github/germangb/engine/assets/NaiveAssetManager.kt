package com.github.germangb.engine.assets

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.graphics.*
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle

class NaiveAssetManager(private val ctx: Context) : AssetManager {
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

    override fun preloadTexture(file: FileHandle, path: String, format: TexelFormat, min: TextureFilter, mag: TextureFilter) {
        if (textures[path] == null) {
            textures[path] = ctx.assets.loadTexture(file, format, min, mag)
        }
    }

    override fun preloadMesh(file: FileHandle, path: String, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>) {
        if (meshes[path] == null) {
            meshes[path] = ctx.assets.loadMesh(file, usage, attributes, instanceAttributes)
        }
    }

    override fun preloadAudio(file: FileHandle, path: String, stream: Boolean) {
        if (audios[path] == null) {
            audios[path] = ctx.assets.loadAudio(file, stream)
        }
    }

    override fun getTexture(path: String) = textures[path]

    override fun getMesh(path: String) = meshes[path]

    override fun getAudio(path: String) = audios[path] ?: let {
        val file = ctx.files.getLocal(path)
        val loaded = ctx.assets.loadAudio(file)
        ctx.assets.loadAudio(file).let { audios[path] = it }
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