package com.github.germangb.engine.backend.lwjgl.core

import com.github.germangb.engine.audio.Audio
import com.github.germangb.engine.core.Backend
import com.github.germangb.engine.core.BufferManager
import com.github.germangb.engine.resources.AssetLoader
import com.github.germangb.engine.graphics.Graphics

/**
 * LWJGL based backend
 */
class LwjglBackend(override val graphics: Graphics,
                   override val audio: Audio,
                   override val resources: AssetLoader,
                   override val buffers: BufferManager) : Backend

