package com.github.germangb.renderer

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.TransformMode
import com.github.germangb.engine.framework.components.addMeshInstance
import com.github.germangb.engine.framework.components.addMeshInstancer
import com.github.germangb.engine.framework.components.instance
import com.github.germangb.engine.graphics.InstanceAttribute.TRANSFORM
import com.github.germangb.engine.graphics.MeshPrimitive.TRIANGLES
import com.github.germangb.engine.graphics.MeshPrimitive.TRIANGLE_STRIP
import com.github.germangb.engine.graphics.MeshUsage.STATIC
import com.github.germangb.engine.graphics.TestFunction.DISABLED
import com.github.germangb.engine.graphics.TestFunction.LESS
import com.github.germangb.engine.graphics.TexelFormat.*
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.graphics.TextureFilter.LINEAR
import com.github.germangb.engine.graphics.VertexAttribute.*
import com.github.germangb.engine.graphics.uniformMap
import com.github.germangb.engine.input.KeyboardKey.KEY_GRAVE_ACCENT
import com.github.germangb.engine.input.isJustPressed
import com.github.germangb.engine.math.FrustumIntersection
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.plugin.bullet.PhysicsWorld
import com.github.germangb.engine.plugins.debug.debug
import com.github.germangb.engine.utils.Destroyable
import com.github.germangb.engine.utils.DummyMesh
import com.github.germangb.engine.utils.DummyTexture
import com.github.germangb.player.Agent
import com.github.germangb.shooter.GameListener

class GraphicsRenderer(private val ctx: Context, private val world: PhysicsWorld, val assets: AssetManager) : GameListener, Destroyable {
    init {
        val terrainFile = ctx.files.getLocal("textures/terrain_debug.jpg")
        val capsuleFile = ctx.files.getLocal("meshes/capsule.obj")

        assets.preloadTexture(terrainFile, "terrain_debug", RGB4, LINEAR, LINEAR, genMips = true)
        assets.preloadMesh(capsuleFile, "capsule_debug", STATIC, arrayOf(POSITION, NORMAL, UV), arrayOf(TRANSFORM))
    }

    /** Instancing buffer */
    private val instanceData = ctx.buffers.create(4096 * 16 * 4)
    /** Simple shader */
    private val capsuleShader = let {
        val file = ctx.files.getLocal("shaders/simple.glsl")
        ctx.graphics.createShaderProgram(file.read()?.use {
            it.bufferedReader().readText()
        } ?: "")
    }
    /** Render target */
    private val fbo = let {
        val (width, height) = ctx.graphics.dimensions
        ctx.graphics.createFramebuffer(width, height, arrayOf(RGB8, DEPTH16))
    }
    /** Composite shader */
    private val compositeShader = let {
        val file = ctx.files.getLocal("shaders/composite.glsl")
        ctx.graphics.createShaderProgram(file.read()?.use {
            it.bufferedReader().readText()
        } ?: "")
    }
    /** Quad to render the final image */
    private val quadMesh = let {
        val vertex = ctx.buffers.create(2 * 4 * 4)
        val index = ctx.buffers.create(4 * 4).asIntBuffer()
        index.put(intArrayOf(0, 1, 2, 3)).flip()
        vertex.putFloat(0f).putFloat(0f)
        vertex.putFloat(1f).putFloat(0f)
        vertex.putFloat(0f).putFloat(1f)
        vertex.putFloat(1f).putFloat(1f).flip()
        val mesh = ctx.graphics.createMesh(vertex, index, TRIANGLE_STRIP, STATIC, arrayOf(POSITION2))
        vertex.clear()
        index.clear()
        ctx.buffers.free(vertex)
        ctx.buffers.free(index)
        mesh
    }

    /** Scene graph */
    val root = Actor()
    /** Agents stuff */
    val agents = root.attachChild {
        addMeshInstancer(assets.getMesh("capsule_debug") ?: DummyMesh, emptyMap())
    }

    //
    // Terrain rendering
    //

    /** Terrain texture */
    var terrainTexture: Texture = DummyTexture
    /** Terrain maximum height */
    var terrainHeight = 16f
    /** Size of the terrain */
    var terrainSize = 2048

    /** Size of a single terrainTexture patch */
    private val terrainChunkSize = 32
    /** Terrain mesh patch */
    private val terrainMesh = let {
        val vert = ctx.buffers.create(100000)
        val indx = ctx.buffers.create(100000).asIntBuffer()
        for (x in 0 until terrainChunkSize + 1) {
            for (z in 0 until terrainChunkSize + 1) {
                // put vertex positions
                vert.putFloat(x.toFloat() - terrainChunkSize / 2)
                vert.putFloat(z.toFloat() - terrainChunkSize / 2)

                // put vertex indices
                if (x < terrainChunkSize && z < terrainChunkSize) {
                    val offset = z * terrainChunkSize + z
                    indx.put(offset + x)
                    indx.put(offset + x + 1 + terrainChunkSize + 1)
                    indx.put(offset + x + terrainChunkSize + 1)
                    indx.put(offset + x)
                    indx.put(offset + x + 1 + terrainChunkSize + 1)
                    indx.put(offset + x + 1)
                }
            }
        }

        vert.flip()
        indx.flip()
        val mesh = ctx.graphics.createMesh(vert, indx, TRIANGLES, STATIC, arrayOf(POSITION2), arrayOf(TRANSFORM))
        vert.clear()
        indx.clear()
        ctx.buffers.free(vert)
        ctx.buffers.free(indx)
        mesh
    }
    /** Terrain shader */
    private val terrainShader = let {
        val file = ctx.files.getLocal("shaders/terrain.glsl")
        ctx.graphics.createShaderProgram(file.read()?.use {
            it.bufferedReader().readText()
        } ?: "")
    }
    /** Terrain debug texture */
    private val terrainDebug = assets.getTexture("terrain_debug") ?: DummyTexture

    //
    // Camera stuff
    //

    /** Frustum culling computations */
    private val culling = FrustumIntersection()
    /** Auxiliary matrix */
    private val aux = Matrix4()

    /** Free rendering resources */
    override fun destroy() {
        fbo.destroy()
        compositeShader.destroy()
        quadMesh.destroy()
        capsuleShader.destroy()
        terrainMesh.destroy()
        terrainShader.destroy()

        instanceData.clear()
        ctx.buffers.free(instanceData)
    }

    /** Render game */
    fun render(view: Matrix4c, projection: Matrix4c) {
        if (KEY_GRAVE_ACCENT.isJustPressed(ctx.input)) {
            ctx.debug.toggle()
        }

        ctx.debug.add(buildString {
            ctx.graphics {
                appendln("# textures = ${textures.size}")
                appendln("# meshes = ${meshes.size}")
                appendln("# framebuffers = ${framebuffers.size}")
                appendln("# programs = ${shaderPrograms.size}")
                appendln("# audios = ${ctx.audio.sources.size}")
            }
        })

        //
        // Compute terrainTexture transforms
        //

        val min = Vector3()
        val max = Vector3()
        val aux = aux.set(projection).mul(view)
        culling.set(aux)
        aux.identity()

        val chunks = 16
        instanceData.clear()
        for (x in -chunks..chunks) {
            for (z in -chunks..chunks) {
                // compute aabb of chunk and check if it is inside of camera frustum
                val cf = terrainChunkSize.toFloat()
                min.set(x * cf, 0f, z * cf).sub(cf / 2, terrainHeight, cf / 2)
                max.set(x * cf, 0f, z * cf).add(cf / 2, terrainHeight, cf / 2)
                if (culling.testAab(min, max)) {
                    aux.m30(x * cf)
                    aux.m32(z * cf)

                    val pos = instanceData.position()
                    aux.get(instanceData)
                    instanceData.position(pos + 16 * 4)
                }
            }
        }
        instanceData.flip()

        // render terrainTexture

        ctx.graphics(fbo) {
            state.depthTest(LESS)
            state.clearColor(0.2f, 0.2f, 0.2f, 1f)
            state.clearDepthBuffer()
            state.clearColorBuffer()

        }

        ctx.graphics(fbo) {
            // render terrainTexture
            render(terrainMesh, terrainShader, uniformMap(
                    "u_view" to view,
                    "u_proj" to projection,
                    "u_max_height" to terrainHeight,
                    "u_size" to 2048f,
                    "u_height" to terrainTexture,
                    "u_texture" to terrainDebug
            ), instanceData)

            // Render capsules
            agents.computeTransforms()
            val caps = agents.breadthFirstTraversal()
            instanceData.clear()
            caps.forEach { actor ->
                // update transform
                val agent = actor.get(AgentWrap::class)
                agent?.let { actor.transform.identity().translate(it.agent.position) }

                actor.instance?.let {
                    actor.transform.get(instanceData)
                    instanceData.position(instanceData.position() + 16 * 4)
                }
            }
            instanceData.flip()

            // render capsule
            assets.getMesh("capsule_debug")?.let {
                render(it, capsuleShader, uniformMap(
                        "u_view" to view,
                        "u_proj" to projection
                ), instanceData)
            }
        }

        // render composite image
        ctx.graphics {
            state.depthTest(DISABLED)
            state.clearColorBuffer()
            render(quadMesh, compositeShader, uniformMap("u_texture" to fbo.targets[0]))
        }
    }

    override fun onDeaded(player: Agent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class AgentWrap(val agent: Agent)

    override fun onSpawned(player: Agent) {
        agents.attachChild {
            transformMode = TransformMode.ABSOLUTE
            addComponent(AgentWrap(player))
            addMeshInstance()
        }
    }

    override fun onAttack(player: Agent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCrouched(player: Agent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onWalk(player: Agent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLook(player: Agent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
