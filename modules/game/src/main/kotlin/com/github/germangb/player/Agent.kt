package com.github.germangb.player

import com.github.germangb.engine.math.Vector3c

interface Agent {
    /** If the player is active in the game */
    val isActive: Boolean

    /** if the player has been killed in the game */
    val isDead: Boolean

    /** Agent position */
    val position: Vector3c

    /** Agent target position */
    val target: Vector3c

    /** Agent looking state */
    val look: LookState

    /** Agent walking state */
    val walk: WalkState

    /** Agent crouching state */
    val crouching: CrouchState
}