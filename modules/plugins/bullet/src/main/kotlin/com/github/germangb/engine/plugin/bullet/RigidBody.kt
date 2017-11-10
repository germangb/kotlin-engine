package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.utils.Destroyable

/**
 * Body in bullet simulation
 */
interface RigidBody : Destroyable {
    /** World transform */
    var transform: Matrix4c

    /** Get linear velocity */
    var velocity: Vector3c

    /** Linear factor */
    var linearFactor: Vector3c

    /** Angular factor */
    var angularFactor: Vector3c

    /** Body friction */
    var friction: Float

    /** Body restitution */
    var restitution: Float

    /** User data attached bind bullet world */
    var data: Any?
}
