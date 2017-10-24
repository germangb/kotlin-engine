package com.github.germangb.engine.audio.desktop

import com.github.germangb.engine.utils.Destroyable
import com.github.germangb.engine.math.Vector3c

interface Audio : Destroyable {
    /**
     * Get sound state
     */
    val state: AudioState

    /**
     * audio gain property
     */
    var gain: Float

    /**
     * Audio position
     */
    var position: Vector3c

    /**
     * Audio velocity
     */
    var velocity: Vector3c

    /**
     * Start sound playback
     */
    fun play(loop: Boolean = false)

    /**
     * Pause playback
     */
    fun pause()

    /**
     * Stop sound playback
     */
    fun stop()
}