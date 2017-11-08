package com.github.germangb.engine.core

/** Timing stuff */
interface Time {
    /** Time since start (seconds) */
    val elapsed: Float

    /** Frames per second */
    val fps: Int

    /** Frame duration */
    val delta: Float
}