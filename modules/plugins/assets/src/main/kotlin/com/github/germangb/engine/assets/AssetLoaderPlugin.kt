package com.github.germangb.engine.assets

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.graphics.*

/** Get asset loader */
val Context.assets get() = getModule<AssetLoader>(AssetLoader.MODULE_NAME) ?: UninstalledAssetLoaderPlugin

/**
 * For when plugin is not installed
 */
object UninstalledAssetLoaderPlugin : AssetLoader {
    override fun loadTexture(file: FileHandle, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean) = TODO()
    override fun loadMesh(file: FileHandle, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>) = TODO()
    override fun loadMesh(file: FileHandle, usage: MeshUsage, attributes: Array<out VertexAttribute>) = TODO()
    override fun loadAudio(file: FileHandle, stream: Boolean) = TODO()
}
