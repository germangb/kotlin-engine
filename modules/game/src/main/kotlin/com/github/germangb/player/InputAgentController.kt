package com.github.germangb.player

import com.github.germangb.engine.core.Time
import com.github.germangb.engine.graphics.FramebufferDimensions
import com.github.germangb.engine.input.InputDevice
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.math.cos
import com.github.germangb.engine.math.sin
import com.github.germangb.shooter.Game

/**
 * Agent controller by user input
 */
class InputAgentController(game: Game, val input: InputDevice, val time: Time, val dimensions: FramebufferDimensions) : AgentController(game) {
    /** Following agent */
    lateinit var agent: Agent

    companion object {
        val aux = Vector3(0f)
    }

    val radius: Vector3c = Vector3(12f, 24f, 12f)
    val center = Vector3()

    override fun init() {
        agent = game.spawnPlayer(Vector3(32f, 8f, 16f))
    }

    override fun update() {
        if (agent.isActive) {
            // camera offset
            aux.y = 0f
            aux.x = sin(time.elapsed * 0.27315f + 0.2437f)
            aux.z = cos(time.elapsed * 0.3512462f)

            center.lerp(agent.position, time.delta)
            val eye = Vector3(center).add(radius)

            game.view.setLookAt(eye, aux.mul(0.5f).add(center), Vector3(0f, 1f, 0f))
        }
    }

}