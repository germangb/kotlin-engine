package com.github.germangb.player

import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.plugin.bullet.RigidBody
import com.github.germangb.player.CrouchState.STANDING
import com.github.germangb.player.LookState.EAST
import com.github.germangb.player.WalkState.STOPPED
import com.github.germangb.shooter.GameApplication

/** Local game player agent */
class LocalAgent(val game: GameApplication) : Agent {
    lateinit var body: RigidBody

    var inGround = true

    var _isActive = false
    var _look = EAST
    var _walk = STOPPED
    var _crouching = STANDING
    var _position: Vector3c = Vector3()
    var _target: Vector3c = Vector3()

    val transform = Matrix4()

    override val isActive get() = _isActive

    override var position: Vector3c
        get() = _position
        set(value) {
            _position = value
            game.setPosition(this, value)
        }

    override var target: Vector3c
        get() = _target
        set(value) {
            _target = value
            game.setTarget(this, value)
        }

    override var look
        get() = _look
        set(value) {
            if (value != _look) {
                _look = value
                game.setLook(this, _look)
            }
        }

    override var walk
        get() = _walk
        set(value) {
            if (value != _walk) {
                _walk = value
                game.setWalk(this, _walk)
            }
        }

    override var crouching
        get() = _crouching
        set(value) {
            if (value != _crouching) {
                _crouching = value
                game.setCrouch(this, _crouching)
            }
        }
}