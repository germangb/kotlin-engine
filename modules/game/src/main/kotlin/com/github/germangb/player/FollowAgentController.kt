package com.github.germangb.player

import com.github.germangb.engine.math.Vector3
import com.github.germangb.shooter.Game

class FollowAgentController(game: Game) : AgentController(game) {
    lateinit var agent: Agent
    override fun init() {
        agent = spawnPlayer(Vector3(0f, 16f, 0f))
    }

    override fun update() {
    }

}