package com.github.germangb.engine.core

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.graphics.Graphics
import com.github.germangb.engine.resources.AssetLoader

/**
 * Engine backend
 */
interface Backend {
    /**
     * Graphics backend
     */
    val graphics: Graphics

    /**
     * Audio
     */
    val audio: Audio

    /**
     * Resources backend
     */
    val assets: AssetLoader

    /**
     * Buffer management
     */
    val buffers: BufferManager
}
