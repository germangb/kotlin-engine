package com.github.germangb.player

import com.github.germangb.engine.core.Context
import com.github.germangb.engine.input.KeyboardKey
import com.github.germangb.engine.input.KeyboardKey.*
import com.github.germangb.engine.input.isJustPressed
import com.github.germangb.engine.input.isPressed
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.plugin.bullet.PhysicsWorld
import com.github.germangb.engine.plugins.debug.debug
import com.github.germangb.player.CrouchState.CROUCHING
import com.github.germangb.player.CrouchState.STANDING
import com.github.germangb.player.WalkState.*
import com.github.germangb.shooter.Game
import java.util.*

/** Movement keys */
val keys = arrayOf(KEY_W, KEY_A, KEY_D, KEY_S)

/**
 * Agent controller by user input
 */
class InputAgentController(game: Game, val ctx: Context, val world: PhysicsWorld) : AgentController(game) {
    /** Following agent */
    lateinit var agent: Agent

    companion object {
        val aux = Vector3(0f)
        val auxMat = Matrix4()
    }

    val inputStack = Stack<KeyboardKey>()
    val radius: Vector3c = Vector3(0f, 32f, 18f)
    val center = Vector3()

    override fun init() {
        agent = spawnPlayer(Vector3(8f, 16f, 16f))
        agent.target = Vector3(1f, 0f, 0f)
    }

    fun Matrix4.look(center: Vector3c) {
        val eye = Vector3(center).add(radius)
        setLookAt(eye, center, Vector3(0f, 1f, 0f))
    }

    fun testRay(): Vector3c {
        val origin = Vector3()
        val dir = Vector3()
        val (width, height) = ctx.graphics.dimensions
        val x = ctx.input.mouse.x
        val y = height - ctx.input.mouse.y
        auxMat.set(projection).mul(view).unprojectRay(x.toFloat(), y.toFloat(), intArrayOf(0, 0, width, height), origin, dir)

        // test collision
        val dest = aux.set(origin).add(dir)
        return world.rayTestClosest(origin, dest)?.position ?: agent.position
    }

    fun updateCamera() {
        if (agent.isActive) {
            // camera offset
            val dim = ctx.graphics.dimensions
            val x = (ctx.input.mouse.x - dim.width / 2f) / dim.width * 2
            val y = (ctx.input.mouse.y - dim.height / 2f) / dim.height * 2
            val offset = Vector3(x, 0f, y).mul(8f)
            center.lerp(offset.add(agent.position), ctx.time.delta * 4)
            view.look(center)
        }
    }

    fun updateInput() {
        val moving = keys.any { it.isPressed(ctx.input) }

        // process new crouching state
        if (KEY_LEFT_CONTROL.isJustPressed(ctx.input)) {
            if (agent.crouching == CROUCHING) {
                agent.crouching = STANDING
            } else {
                agent.crouching = CROUCHING
            }
        }

        var walkState = STOPPED

        if (moving) {
            val walk = KEY_LEFT_SHIFT.isPressed(ctx.input)
            walkState = if (walk) WALKING else RUNNING
        }

        agent.walk = walkState

        // collect new input
        keys.filter { it.isJustPressed(ctx.input) }.forEach {
            inputStack.push(it)
        }

        // compute target vector
        if (inputStack.isEmpty()) {
            agent.walk = STOPPED
        } else {
            var key = inputStack.peek()
            while (!key.isPressed(ctx.input) && inputStack.isNotEmpty()) {
                inputStack.pop()
                if (inputStack.isNotEmpty()) {
                    key = inputStack.peek()
                }
            }

            if (key.isPressed(ctx.input)) {
                when {
                    KEY_W == key -> agent.target = Vector3(0f, 0f, -1f)
                    KEY_S == key -> agent.target = Vector3(0f, 0f, 1f)
                    KEY_A == key -> agent.target = Vector3(-1f, 0f, 0f)
                    KEY_D == key -> agent.target = Vector3(1f, 0f, 0f)
                }
            }
        }
    }

    override fun update() {
        ctx.debug.add(buildString {
            appendln("active = ${agent.isActive}")
            appendln("walk = ${agent.walk}")
            appendln("crouching = ${agent.crouching}")
            appendln("-".repeat(3))
        })

        updateCamera()

        if (ctx.input.mouse.insideWindow) {
            //val hitPos = testRay()
        }

        updateInput()
    }

}