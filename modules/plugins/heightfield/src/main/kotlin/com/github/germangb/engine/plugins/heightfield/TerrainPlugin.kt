package com.github.germangb.engine.plugins.heightfield

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import com.github.germangb.engine.core.getPlugin
import com.github.germangb.engine.files.FileHandle

/**
 * Heightfield plugin
 */
interface TerrainPlugin : Plugin, TerrainLoader

/**
 * Fallback for when plugin is not installed
 */
object UninstalledTerrainPlugin : TerrainPlugin {
    override fun load16(file: FileHandle, desiredChannels: Int, createTexture: Boolean) = TODO("Heightfield plugin not installed")
    override fun load8(file: FileHandle, desiredChannels: Int, createTexture: Boolean) = TODO("Heightfield plugin not installed")
    override fun loadf(file: FileHandle, desiredChannels: Int, createTexture: Boolean) = TODO("Heightfield plugin not installed")
}

/** Get terrain plugin */
val Context.terrain get() = (getPlugin<TerrainPlugin>() as? TerrainLoader) ?: UninstalledTerrainPlugin