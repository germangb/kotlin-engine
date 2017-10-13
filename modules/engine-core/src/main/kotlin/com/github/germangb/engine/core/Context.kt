package com.github.germangb.engine.core

import com.github.germangb.engine.assets.AssetLoader
import com.github.germangb.engine.audio.AudioDevice
import com.github.germangb.engine.graphics.GraphicsDevice
import com.github.germangb.engine.input.InputDevice
import kotlin.reflect.KClass

/**
 * Engine backend
 */
interface Context {
    /**
     * GraphicsDevice backend
     */
    val graphics: GraphicsDevice

    /**
     * AudioDevice backend
     */
    val audio: AudioDevice

    /**
     * InputDevice backend
     */
    val input: InputDevice

    /**
     * Resources backend
     */
    val assets: AssetLoader

    /**
     * Buffer management
     */
    val buffers: BufferManager

    /**
     * Get plugin
     */
    fun <T: Plugin> getPlugin(plug: KClass<T>): T?
}
