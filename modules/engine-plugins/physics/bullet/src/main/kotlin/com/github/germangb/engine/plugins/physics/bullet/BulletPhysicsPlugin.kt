package com.github.germangb.engine.plugins.physics.bullet

import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.utils.GdxNativesLoader
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.plugin.physics.PhysicsPlugin
import com.github.germangb.engine.plugin.physics.PhysicsWorld

/**
 * Bullet physics plugin implementation
 */
object BulletPhysicsPlugin : PhysicsPlugin {
    override fun onPreInit() {
        GdxNativesLoader.load()
        Bullet.init()
    }

    override fun onPostInit() = Unit
    override fun onPreUpdate() = Unit
    override fun onPostUpdate() = Unit
    override fun onPreDestroy() = Unit
    override fun onPostDestroy() = Unit

    override fun createWorld(gravity: Vector3c): PhysicsWorld {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}