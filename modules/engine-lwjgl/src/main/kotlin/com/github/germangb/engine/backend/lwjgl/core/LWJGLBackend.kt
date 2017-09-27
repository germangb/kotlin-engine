package com.github.germangb.engine.backend.lwjgl.core

import com.github.germangb.engine.audio.AudioDevice
import com.github.germangb.engine.core.Backend
import com.github.germangb.engine.core.BufferManager
import com.github.germangb.engine.resources.AssetLoader
import com.github.germangb.engine.graphics.GraphicsDevice

/**
 * LWJGL based backend
 */
class LWJGLBackend(override val graphics: GraphicsDevice,
                   override val audio: AudioDevice,
                   override val assets: AssetLoader,
                   override val buffers: BufferManager) : Backend

