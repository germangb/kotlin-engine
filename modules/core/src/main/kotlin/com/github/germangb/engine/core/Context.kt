package com.github.germangb.engine.core

import com.github.germangb.engine.audio.AudioDevice
import com.github.germangb.engine.files.Files
import com.github.germangb.engine.graphics.GraphicsDevice
import com.github.germangb.engine.input.InputDevice
import kotlin.reflect.KClass

/**
 * Engine backend
 */
interface Context {
    /** GraphicsDevice backend */
    val graphics: GraphicsDevice

    /** AudioDevice backend */
    val audio: AudioDevice

    /** InputDevice backend */
    val input: InputDevice

    /** Files backend */
    val files: Files

    /** Buffer management */
    val buffers: BufferManager

    /** Timing values */
    val time: Time

    /** Get a plugin that extends the capabilities of the framework */
    fun <T> getModule(name: String): T?
}
