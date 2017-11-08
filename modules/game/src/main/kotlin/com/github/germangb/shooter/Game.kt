package com.github.germangb.shooter

import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.player.CrouchState
import com.github.germangb.player.LookState
import com.github.germangb.player.Agent
import com.github.germangb.player.WalkState

/**
 * Listen to game events
 */
interface GameListener {
    /** Called when player is deaded */
    fun onDeaded(player: Agent)

    /** Called when a player is spawned */
    fun onSpawned(player: Agent)

    /** Called when a player attacks */
    fun onAttack(player: Agent)

    /** Called when player crouching state changes */
    fun onCrouched(player: Agent)

    /** Called when player walking state changes */
    fun onWalk(player: Agent)

    /** Called when player look state changes */
    fun onLook(player: Agent)
}

/** API to interact with the game itself */
interface Game {
    /** DebugCamera view matrix */
    val view: Matrix4

    /** DebugCamera projection matrix */
    val projection: Matrix4

    /** Spawn a new player in the game */
    fun spawnPlayer(location: Vector3c): Agent

    /** Attack with a payer agent to a given location */
    fun attack(player: Agent, location: Vector3c)

    /** Set crouching state of a player agent */
    fun setCrouch(player: Agent, state: CrouchState)

    /** Set moving state of a player agent */
    fun setWalk(player: Agent, state: WalkState)

    /** Set looking state of a player agent */
    fun setLook(player: Agent, state: LookState)

    /** Suicide one of your agents */
    fun suicide(player: Agent)

    /** Set target */
    fun setTarget(player: Agent, target: Vector3c)

    /** Test line of sight */
    fun testLineOfSight(from: Vector3c, to: Vector3c): Boolean

    /** Register game listener */
    fun addListener(listener: GameListener)
}