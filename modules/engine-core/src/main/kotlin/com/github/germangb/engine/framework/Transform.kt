package com.github.germangb.engine.framework

import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c

class Transform {
    /**
     * Transform relative to parent
     */
    val local = Matrix4()

    /**
     * World transform (used by framework)
     */
    internal val iworld = Matrix4()

    /**
     * World transform
     */
    val world: Matrix4c get() = iworld
}