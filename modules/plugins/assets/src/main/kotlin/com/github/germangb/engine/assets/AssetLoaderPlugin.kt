package com.github.germangb.engine.assets

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import com.github.germangb.engine.graphics.MeshUsage
import com.github.germangb.engine.graphics.TexelFormat
import com.github.germangb.engine.graphics.TextureFilter
import com.github.germangb.engine.graphics.VertexAttribute

/**
 * Asset loader plugin
 */
interface AssetLoaderPlugin : Plugin, AssetLoader

/**
 * For when plugin is not installed
 */
object UninstalledAssetLoaderPlugin : AssetLoader {
    override fun loadTexture(path: String, format: TexelFormat, min: TextureFilter, mag: TextureFilter) = TODO()
    override fun loadMesh(path: String, usage: MeshUsage, vararg attributes: VertexAttribute) = TODO()
    override fun loadAudio(path: String, stream: Boolean) = TODO()
}

/**
 * Get asset loader
 */
val Context.assets get() = (getPlugin(AssetLoaderPlugin::class) as? AssetLoader) ?: UninstalledAssetLoaderPlugin