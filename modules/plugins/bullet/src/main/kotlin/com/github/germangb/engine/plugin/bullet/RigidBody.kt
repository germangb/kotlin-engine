package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Vector3c

/** How to treat the activation/deactivation of an abject. */
enum class ActivationState {
    /** Default */
    ACTIVE_TAG,

    /** Here for completion. Refer to official bullet documentation */
    ISLAND_SLEEPING,

    /** Here for completion. Refer to official bullet documentation */
    WANTS_DEACTIVATION,

    /** Body is never deactivated. Good for kinematic bodies */
    DISABLE_DEACTIVATION,

    /** Body is always deactivated */
    DISABLE_SIMULATION
}

/** Body in bullet simulation */
interface RigidBody {
    /** Static collision flag */
    var isStatic: Boolean

    /** Kinematic collision flag */
    var isKinematic: Boolean

    /** Character object flag */
    var isCharacterObject: Boolean

    /** Activation state */
    var activationState: ActivationState

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

    /** Body motion state */
    val motionState: MotionState

    /** Clear body forces */
    fun clearForces()

    /** Activate rigid body */
    fun activate()
}
