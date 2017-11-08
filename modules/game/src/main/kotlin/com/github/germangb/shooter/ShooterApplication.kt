package com.github.germangb.shooter

import com.github.germangb.engine.assets.NaiveAssetManager
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.math.toRadians
import com.github.germangb.engine.plugin.bullet.bullet
import com.github.germangb.engine.plugins.heightfield.terrain
import com.github.germangb.engine.utils.DummyTexture
import com.github.germangb.player.*
import com.github.germangb.renderer.AudioRenderer
import com.github.germangb.renderer.GraphicsRenderer
import java.util.*

/** Base application */
class ShooterApplication(val ctx: Context) : Application, Game {
    /** Asset manager */
    val assets = NaiveAssetManager(ctx)

    /** Bullet physics world */
    val world = ctx.bullet.createWorld(Vector3(0f, -10f, 0f))
    /** Keeps track of residual simulation elapsed */
    var simTime = 0f

    /** Terrain maximum height */
    val terrainHeight = 32f
    /** Loaded terrain heightfield */
    val terrain = let {
        val file = ctx.files.getLocal("textures/heightfield.png")
        ctx.terrain.load16(file, 1, createTexture = true)
    }

    /** View matrix */
    override val view = Matrix4()
    /** Projection matrix */
    override val projection = Matrix4()

    /** Graphics Renderer */
    val graphics = GraphicsRenderer(ctx, world, assets)
    /** Audio Renderer */
    val audio = AudioRenderer(ctx, assets)

    /** Game listeners */
    val listeners = mutableListOf<GameListener>()
    /** Pending agents */
    val pending = LinkedList<LocalAgent>()
    /** Agent controllers */
    val agentControllers = mutableListOf<AgentController>()

    /** Register agent controllers */
    fun registerControllers() {
        val dim = ctx.graphics.dimensions
        addController(InputAgentController(this, ctx.input, ctx.time, dim))
    }

    /** Set core listeners */
    fun initListeners() {
        addListener(graphics)
        addListener(audio)
        audio.init()
    }

    /** Initialize agent controllers */
    fun initControllers() = agentControllers.forEach { it.init() }

    /** Default camera transforms */
    fun initCamera() {
        val (width, height) = ctx.graphics.dimensions
        val aspect = width.toFloat() / height
        projection.setPerspective(toRadians(50f), aspect, 0.1f, 512f)
        //view.setLookAt(Vector3(16f, 8f, 16f), Vector3(0f), Vector3(0f, 1f, 0f))
    }

    /** Init terrain in renderer */
    fun initTerrain() {
        graphics.terrainHeight = terrainHeight
        graphics.terrainTexture = terrain?.texture ?: DummyTexture

        // init physics terrain
        terrain?.let {
            val size = terrain.size
            val shape = ctx.bullet.createHeightfield(size, size, terrain.data, terrainHeight / Short.MAX_VALUE, -terrainHeight, terrainHeight)
            world.createBody(shape, false, 0f, 0.5f, 0f, Matrix4())
            graphics.terrainSize = size
        }
    }

    /** Init game resources */
    override fun init() {
        initListeners()
        initCamera()
        initTerrain()
        registerControllers()
        initControllers()
    }

    /** Spawn agents on demand */
    fun handleSpawns() {
        while (pending.isNotEmpty()) {
            //TODO activate, register, and spawn agent
            val agent = pending.poll()
            agent._isActive = true
            listeners.forEach { it.onSpawned(agent) }
        }
    }

    /** Step & update physics simulation */
    fun handlePhysics() {
        val step = 1 / 60f
        simTime += ctx.time.delta
        while (simTime >= step) {
            simTime -= step
            world.stepSimulation(step)
        }
    }

    /** Update game state */
    override fun update() {
        handleSpawns()
        handlePhysics()

        // update agent controllers
        agentControllers.forEach { it.update() }

        // render graphics
        graphics.render(view, projection)
    }

    override fun suicide(player: Agent) {
        if (player !is LocalAgent) throw Exception()
        player._isDead = true
        listeners.forEach { it.onDeaded(player) }
        TODO()
    }

    override fun attack(player: Agent, location: Vector3c) {
        if (player !is LocalAgent) throw Exception()
        listeners.forEach { it.onAttack(player) }
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCrouch(player: Agent, state: CrouchState) {
        if (player !is LocalAgent) throw Exception()
        player._crouching = state
        listeners.forEach { it.onCrouched(player) }
    }

    override fun setLook(player: Agent, state: LookState) {
        if (player !is LocalAgent) throw Exception()
        player._look = state
        listeners.forEach { it.onLook(player) }
    }

    override fun setWalk(player: Agent, state: WalkState) {
        if (player !is LocalAgent) throw Exception()
        player._walk = state
        listeners.forEach { it.onWalk(player) }
    }

    override fun setTarget(player: Agent, target: Vector3c) {
        if (player !is LocalAgent) throw Exception()
        player.target.set(target)
    }

    override fun testLineOfSight(from: Vector3c, to: Vector3c): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun spawnPlayer(location: Vector3c): Agent {
        val agent = LocalAgent()
        agent.position.set(location)
        pending.add(agent)
        return agent
    }

    override fun addListener(listener: GameListener) {
        listeners.add(listener)
    }

    /**
     * Register an agent controler
     */
    fun addController(controller: AgentController) {
        agentControllers.add(controller)
    }

    /** Cleanup game resources */
    override fun destroy() {
        graphics.destroy()
        audio.destroy()
        assets.destroy()
        world.destroy()
        terrain?.destroy()
    }
}
