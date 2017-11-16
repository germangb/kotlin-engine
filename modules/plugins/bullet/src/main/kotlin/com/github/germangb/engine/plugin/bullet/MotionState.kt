package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c

/**
 * Default motion state (similar to bullet's default)
 */
class DefaultMotionState(val initial: Matrix4c) : MotionState {
    /** Instantiate with identity transform */
    constructor() : this(aux)

    companion object {
        private val aux = Matrix4()
    }

    override var worldTransform: Matrix4c
        get() = initial
        set(value) = Unit
}

interface MotionState {
    /** Get/set world transform */
    var worldTransform: Matrix4c
}