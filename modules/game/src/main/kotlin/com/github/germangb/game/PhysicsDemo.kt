package com.github.germangb.game

import com.github.germangb.engine.assets.NaiveAssetManager
import com.github.germangb.engine.core.Application
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.TransformMode.ABSOLUTE
import com.github.germangb.engine.framework.components.addMesh
import com.github.germangb.engine.framework.components.addUpdate
import com.github.germangb.engine.framework.components.mesh
import com.github.germangb.engine.framework.components.updater
import com.github.germangb.engine.graphics.MeshUsage.STATIC
import com.github.germangb.engine.graphics.TestFunction.LESS
import com.github.germangb.engine.graphics.VertexAttribute.*
import com.github.germangb.engine.graphics.uniformMap
import com.github.germangb.engine.math.*
import com.github.germangb.engine.plugin.bullet.bullet
import com.github.germangb.engine.utils.DummyMesh

class PhysicsDemo(val ctx: Context) : Application {
    val assets = NaiveAssetManager(ctx)

    companion object {
        val WORLD_FLAG = 0x1
        val PROPS_FLAG = 0x2
    }

    init {
        listOf("capsule", "cube", "cylinder", "plane", "sphere").forEach {
            val file = ctx.files.getLocal("solids/$it.obj")
            assets.preloadMesh(file, it, STATIC, arrayOf(POSITION, NORMAL, UV))
        }
    }

    // camera
    val view = Matrix4()
    val projection = Matrix4()

    // physics world
    val world = ctx.bullet.createWorld(Vector3(0f, -10f, 0f))
    val cube = ctx.bullet.createBox(Vector3(1f))
    val cubeBodyA = world.addRigidBody(cube, 1f, PROPS_FLAG, WORLD_FLAG or PROPS_FLAG)
    val cubeBodyB = world.addRigidBody(cube, 0f, PROPS_FLAG, WORLD_FLAG or PROPS_FLAG)

    var acum = 0f

    // rendering
    val shader = ctx.files.getLocal("solids/shader.glsl").read().use {
        val fb = ""
        ctx.graphics.createShaderProgram(it?.bufferedReader()?.readText() ?: fb)
    }

    // scene
    val root = Actor()

    override fun init() {
        val plane = ctx.bullet.createBox(Vector3(16f, 0.01f, 16f))
        world.addRigidBody(plane, 0f, WORLD_FLAG, WORLD_FLAG)

        root.attachChild {
            cubeBodyA.transform = Matrix4().translate(0f, -8f, 0f).rotateX(0.1f).rotateY(0.2f)
            transformMode = ABSOLUTE
            addMesh(assets.getMesh("cube") ?: DummyMesh, emptyMap())
            addUpdate { transform.set(cubeBodyA.transform) }
        }

        root.attachChild {
            transformMode = ABSOLUTE
            addMesh(assets.getMesh("cube") ?: DummyMesh, emptyMap())
            addUpdate { transform.set(cubeBodyB.transform) }
        }

        // constraint
        val const = ctx.bullet.createPoint2PointContraint(cubeBodyA, cubeBodyB, Vector3(0f, 4f, 0f), Vector3(0f, -4f, 0f))
        world.addConstraint(const)
    }

    override fun update() {
        val transf = Matrix4().translate(sin(ctx.time.elapsed * 2f) * 2f, sin(ctx.time.elapsed*16f), cos(ctx.time.elapsed * 2f) * 2f)
        cubeBodyB.transform = transf
        cubeBodyB.motionState.worldTransform = transf
        cubeBodyB.clearForces()
        cubeBodyB.activate()

        updatePhysics()
        updateCamera()
        updateScene()

        renderScene(projection, view)
    }

    fun renderScene(projection: Matrix4c, view: Matrix4c) {
        ctx.graphics {
            depthTest(LESS)
            clearColor(0.2f, 0.2f, 0.2f, 1f)
            clearColorBuffer()
            clearDepthBuffer()

            val bfs = root.breadthFirstTraversal()
            bfs.forEach { actor ->
                actor.mesh?.let {
                    render(it.mesh, shader, uniformMap(
                            "u_projection" to projection,
                            "u_view" to view,
                            "u_model" to actor.worldTransform
                    ))
                }
            }
        }
    }

    fun updateScene() {
        val bfs = root.breadthFirstTraversal()
        bfs.mapNotNull { it.updater?.update }.forEach { it() }
        root.computeTransforms()
    }

    fun updateCamera() {
        val (w, h) = ctx.graphics.dimensions
        projection.setPerspective(toRadians(50f), w / h.toFloat(), 0.01f, 1024f)
        view.setLookAt(4f, 8f, 12f, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    fun updatePhysics() {
        acum += ctx.time.delta
        while (acum > 1 / 60f) {
            world.stepSimulation(1 / 60f)
            acum -= 1 / 60f
        }
    }

    override fun destroy() {
        assets.destroy()
        world.destroy()
        shader.destroy()
    }

}