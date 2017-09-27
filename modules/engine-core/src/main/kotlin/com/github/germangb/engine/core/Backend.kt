package com.github.germangb.engine.core

import com.github.germangb.engine.audio.AudioDevice
import com.github.germangb.engine.graphics.GraphicsDevice
import com.github.germangb.engine.resources.AssetLoader

/**
 * Engine backend
 */
interface Backend {
    /**
     * GraphicsDevice backend
     */
    val graphics: GraphicsDevice

    /**
     * AudioDevice
     */
    val audio: AudioDevice

    /**
     * Resources backend
     */
    val assets: AssetLoader

    /**
     * Buffer management
     */
    val buffers: BufferManager
}
