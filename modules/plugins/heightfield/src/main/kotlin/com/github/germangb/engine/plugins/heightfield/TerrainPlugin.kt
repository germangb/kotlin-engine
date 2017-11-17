package com.github.germangb.engine.plugins.heightfield

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.plugins.heightfield.TerrainLoader.Companion.MODULE_NAME

/**
 * Fallback for when plugin is not installed
 */
object UninstalledTerrainPlugin : TerrainLoader {
    override fun load16(file: FileHandle, desiredChannels: Int, createTexture: Boolean) = TODO("Heightfield plugin not installed")
    override fun load8(file: FileHandle, desiredChannels: Int, createTexture: Boolean) = TODO("Heightfield plugin not installed")
    override fun loadf(file: FileHandle, desiredChannels: Int, createTexture: Boolean) = TODO("Heightfield plugin not installed")
}

/** Get terrain plugin */
val Context.terrain get() = getModule<TerrainLoader>(MODULE_NAME) ?: UninstalledTerrainPlugin