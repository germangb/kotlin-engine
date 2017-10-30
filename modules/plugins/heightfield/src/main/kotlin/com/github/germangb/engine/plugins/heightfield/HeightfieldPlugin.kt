package com.github.germangb.engine.plugins.heightfield

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.core.Plugin
import com.github.germangb.engine.files.FileHandle

/**
 * Heightfield plugin
 */
interface HeightfieldPlugin : Plugin, HeightfieldLoader

/**
 * Fallback for when plugin is not installed
 */
object UninstalledHeightfieldPlugin : HeightfieldPlugin {
    override fun load16(file: FileHandle, desiredChannels: Int, createTexture: Boolean) = TODO("Heightfield plugin not installed")
}

/** Get heightfield plugin */
val Context.heightfield get() = (getPlugin(HeightfieldPlugin::class) as? HeightfieldLoader) ?: UninstalledHeightfieldPlugin