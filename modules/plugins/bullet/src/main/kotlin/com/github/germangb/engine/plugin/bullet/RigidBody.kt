package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.utils.Destroyable
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
     * User data attached bind bullet world
     */
    var data: Any?
}
