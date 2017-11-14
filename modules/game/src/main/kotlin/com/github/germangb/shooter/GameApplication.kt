package com.github.germangb.shooter

import com.github.germangb.engine.assets.NaiveAssetManager
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.math.*
import com.github.germangb.engine.plugin.bullet.bullet
import com.github.germangb.engine.plugins.heightfield.terrain
import com.github.germangb.engine.utils.DummyTexture
import com.github.germangb.player.*
import com.github.germangb.player.CrouchState.STANDING
import com.github.germangb.player.WalkState.RUNNING
import com.github.germangb.player.WalkState.STOPPED
import com.github.germangb.renderer.AudioRenderer
import com.github.germangb.renderer.GraphicsRenderer
import java.util.*

/** Environment collision flag */
private val MAP_COLLISION = 0x4
/** Agent collision flag */
private val AGENT_COLLISION = 0x2

/** Base application */
class GameApplication(val ctx: Context) : Application, Game {
    /** Asset manager */
    val assets = NaiveAssetManager(ctx)

    /** Bullet physics world */
    val world = ctx.bullet.createWorld(Vector3(0f, -10f, 0f))
    /** Keeps track of residual simulation elapsed */
    var simTime = 0f

    /** Terrain maximum height */
    val terrainHeight = 16f
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
    /** Active agents */
    val activeAgents = LinkedList<LocalAgent>()
    /** Agent controllers */
    val agentControllers = mutableListOf<AgentController>()

    /** Register agent controllers */
    fun registerControllers() {
        addController(InputAgentController(this, ctx, world))
        addController(FollowAgentController(this))
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
            val body = world.addRigidBody(shape, 0f, MAP_COLLISION, AGENT_COLLISION)

            body.friction = 0.5f
            body.restitution = 0f
            graphics.terrainSize = size
        } ?: TODO("Create flat plane")
    }

    /** Init game resources */
    override fun init() {
        initListeners()
        initCamera()
        initTerrain()
        registerControllers()
        initControllers()

        // debug buildings
        addBuilding(Vector3(14f, 6f, 8f), Vector3(12f, 0f, 0f), Quaternion().rotateY(0.15f))
        addBuilding(Vector3(10f, 6f, 8f), Vector3(24f, 0f, 8f), Quaternion().rotateY(0.2f))
        addBuilding(Vector3(10f, 6f, 8f), Vector3(24f, 0f, 38f), Quaternion().rotateY(0.1f))
        addBuilding(Vector3(10f, 6f, 5f), Vector3(32f, 0f, 30f), Quaternion().rotateY(0.1f))
    }

    /** Add a debug block */
    private fun addBuilding(half: Vector3c, position: Vector3c, rotation: Quaternionc) {
        val transform = Matrix4()

        transform.translate(position).rotate(rotation)

        val shape = ctx.bullet.createBox(half)
        val body = world.addRigidBody(shape, 0f, MAP_COLLISION, AGENT_COLLISION)
        body.friction = 0.5f
        body.restitution = 0f
        body.transform = transform

        transform.scale(half)
        graphics.addBuilding(transform)
    }

    /** Spawn agents on demand */
    fun handleSpawns() {
        while (pending.isNotEmpty()) {
            //TODO activate, register, and spawn agent
            val agent = pending.poll()

            // create rigid body
            val transf = Matrix4().translate(agent.position)
            val shape = ctx.bullet.createCapsule(0.5f, 1f)
            agent.body = world.addRigidBody(shape, 1f, AGENT_COLLISION, MAP_COLLISION)

            agent.body.friction = 1f
            agent.body.restitution = 0f
            agent.body.transform = transf
            agent.body.angularFactor = Vector3(0f, 0f, 0f)

            agent._isActive = true
            activeAgents.add(agent)
            listeners.forEach { it.onSpawned(agent) }
        }
    }

    /** Step & update physics simulation */
    fun handlePhysics() {
        val step = 1 / 120f
        simTime += ctx.time.delta
        while (simTime >= step) {
            simTime -= step
            world.stepSimulation(step)
        }
    }

    /** Checks if we can make the agent move */
    fun LocalAgent.canWalk() = walk != STOPPED && crouching == STANDING && inGround

    /** Update agents */
    fun handleAgents() {
        val aux = Vector3()
        activeAgents.forEach {
            //TODO check if agent is grounded
            //it.grounded = ...

            if (it.canWalk()) {
                it.body.friction = 0.5f
                val velo = it.body.velocity
                aux.set(it.target)
                aux.y = 0f

                if (aux.lengthSquared() > 0.001f) {
                    aux.normalize()
                    aux.mul(if (it.walk == RUNNING) 6f else 3f)
                }

                aux.y = velo.y
                it.body.velocity = aux
            } else {
                it.body.friction = 4f
            }

            // update position
            it.body.transform.getTranslation(it.position as Vector3)
            it.body.transform.get(it.transform)
        }
    }

    /** Update game state */
    override fun update() {
        handleSpawns()
        handleAgents()
        handlePhysics()

        // update agent controllers
        agentControllers.forEach { it.update() }

        // render graphics
        graphics.render(view, projection)
    }

    fun setCrouch(player: Agent, state: CrouchState) {
        if (player !is LocalAgent) throw Exception()
        player._crouching = state
        listeners.forEach { it.onCrouched(player) }
    }

    fun setLook(player: Agent, state: LookState) {
        if (player !is LocalAgent) throw Exception()
        player._look = state
        listeners.forEach { it.onLook(player) }
    }

    fun setWalk(player: Agent, state: WalkState) {
        if (player !is LocalAgent) throw Exception()
        player._walk = state
        listeners.forEach { it.onWalk(player) }
    }

    fun setTarget(player: Agent, target: Vector3c) {
        if (player !is LocalAgent) throw Exception()
    }

    fun setPosition(player: Agent, target: Vector3c) {
        if (player !is LocalAgent) throw Exception()
    }

    override fun spawnPlayer(location: Vector3c): Agent {
        val agent = LocalAgent(this)
        (agent.position as Vector3).set(location)
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
