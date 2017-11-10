package com.github.germangb.shooter

import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.player.Agent

/**
 * Listen to game events
 */
interface GameListener {
    /** Called when a player is spawned */
    fun onSpawned(player: Agent)

    /** Called when player crouching state changes */
    fun onCrouched(player: Agent)

    /** Called when player walking state changes */
    fun onWalk(player: Agent)

    /** Called when player look state changes */
    fun onLook(player: Agent)

    /** Called when agent position is updated */
    fun onPosition(player: Agent)

    /** Called when target is updated */
    fun onTarget(player: Agent)
}

/** API to interact with the game itself */
interface Game {
    /** DebugCamera view matrix */
    val view: Matrix4

    /** DebugCamera projection matrix */
    val projection: Matrix4

    /** Spawn a new player in the game */
    fun spawnPlayer(location: Vector3c): Agent

    /** Register game listener */
    fun addListener(listener: GameListener)
}