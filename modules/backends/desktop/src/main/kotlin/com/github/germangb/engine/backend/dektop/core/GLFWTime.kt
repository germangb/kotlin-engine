package com.github.germangb.engine.backend.dektop.core

import com.github.germangb.engine.core.Time

/** Timing stuff */
class GLFWTime : Time {
    var _elapsed = 0f
    var _fps = 0
    var _delta = 0f

    override val elapsed get() = _elapsed
    override val fps get() = _fps
    override val delta get() = _delta

}