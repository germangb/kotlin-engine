package com.github.germangb.engine.assets

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.graphics.*

/**
 * Asset loader plugin
 */
interface AssetLoaderPlugin : Plugin, AssetLoader

/**
 * For when plugin is not installed
 */
object UninstalledAssetLoaderPlugin : AssetLoader {
    override fun loadTexture(file: FileHandle, format: TexelFormat, min: TextureFilter, mag: TextureFilter) = TODO()
    override fun loadMesh(file: FileHandle, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>) = TODO()
    override fun loadAudio(path: FileHandle, stream: Boolean) = TODO()
}

/**
 * Get asset loader
 */
val Context.assets get() = (getPlugin(AssetLoaderPlugin::class) as? AssetLoader) ?: UninstalledAssetLoaderPlugin