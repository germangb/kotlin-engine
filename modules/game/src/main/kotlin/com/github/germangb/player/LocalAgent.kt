package com.github.germangb.player

import com.github.germangb.engine.math.Vector3
import com.github.germangb.player.CrouchState.STANDING
import com.github.germangb.player.LookState.EAST
import com.github.germangb.player.WalkState.STOPPED

/** Local game player agent */
class LocalAgent : Agent {
    var _isActive = false
    var _isDead = false
    var _look = EAST
    var _walk = STOPPED
    var _crouching = STANDING

    override val target = Vector3()
    override val position = Vector3()

    override val isActive get() = _isActive
    override val isDead get() = _isDead
    override val look get() = _look
    override val walk get() = _walk
    override val crouching get() = _crouching
}