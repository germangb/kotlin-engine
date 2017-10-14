package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.core.Destroyable
import com.github.germangb.engine.math.Matrix4c

/**
 * Body in bullet simulation
 */
interface RigidBody: Destroyable {
    /**
     * World transform
     */
    val transform: Matrix4c

    /**
     * User data attached to bullet world
     */
    var data: Any?
}
