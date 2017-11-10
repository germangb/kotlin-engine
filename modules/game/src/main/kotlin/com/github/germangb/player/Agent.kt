package com.github.germangb.player

import com.github.germangb.engine.math.Vector3c

interface Agent {
    /** If the player is active in the game */
    val isActive: Boolean

    /** Agent position */
    var position: Vector3c

    /** Agent target position */
    var target: Vector3c

    /** Agent looking state */
    var look: LookState

    /** Agent walking state */
    var walk: WalkState

    /** Agent crouching state */
    var crouching: CrouchState
}