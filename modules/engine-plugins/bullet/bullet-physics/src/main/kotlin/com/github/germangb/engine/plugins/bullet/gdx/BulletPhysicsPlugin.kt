package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.utils.GdxNativesLoader
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.plugin.bullet.BulletPlugin
import com.github.germangb.engine.plugin.bullet.PhysicsWorld

/**
 * Bullet bullet plugin implementation
 */
class BulletPhysicsPlugin(val ctx: Context) : BulletPlugin {
    val worlds = mutableListOf<BulletPhysicsWorld>()

    override fun onPreInit() {
        GdxNativesLoader.load()
        Bullet.init()
    }

    /**
     * Register a new bullet world
     */
    override fun createWorld(gravity: Vector3c): PhysicsWorld {
        val world = BulletPhysicsWorld(this)
        worlds.add(world)
        return world
    }

    // TODO Logging

    override fun onPostInit() = Unit
    override fun onPreUpdate() = Unit
    override fun onPostUpdate() = Unit
    override fun onPreDestroy() = Unit
    override fun onPostDestroy() = Unit
}