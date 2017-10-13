package com.github.germangb.engine.plugin.physics

import com.github.germangb.engine.core.Destroyable
import com.github.germangb.engine.math.Matrix4c

/**
 * Body in physics simulation
 */
interface RigidBody: Destroyable {
    /**
     * World transform
     */
    val transform: Matrix4c

    /**
     * User data attached to physics world
     */
    var data: Any?
}
