package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.linearmath.btMotionState
import com.github.germangb.engine.math.Matrix4

class BulletMotionState(val matrix: Matrix4) : btMotionState() {

    /**
     * Get world transform for rendering purposes
     */
    override fun getWorldTransform(worldTrans: GdxMatrix4?) {
        worldTrans?.set(matrix)
    }

    /**
     * Set transform for rendering purposes
     */
    override fun setWorldTransform(worldTrans: GdxMatrix4?) {
        worldTrans?.let { matrix.set(worldTrans) }
    }
}