package com.github.germangb.engine.plugin.physics

import com.github.germangb.engine.core.Destroyable

/**
 * Body in physics simulation
 */
interface RigidBody: Destroyable {
    /**
     * User data attached to physics world
     */
    var data: Any?
}
