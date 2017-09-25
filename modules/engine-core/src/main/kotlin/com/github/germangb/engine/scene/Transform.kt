package com.github.germangb.engine.scene

import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c

class Transform {
    /**
     * Transform relative to parent
     */
    val local = Matrix4()

    /**
     * World transform (used by scene)
     */
    internal val iworld = Matrix4()

    /**
     * World transform
     */
    val world: Matrix4c get() = iworld
}