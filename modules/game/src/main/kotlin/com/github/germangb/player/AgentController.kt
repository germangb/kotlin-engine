package com.github.germangb.player

import com.github.germangb.shooter.Game

/** Controll player state */
abstract class AgentController(protected val game: Game) {
    /** Called a the beginning of the game */
    abstract fun init()

    /** Update player (called evert game tick) */
    abstract fun update()
}