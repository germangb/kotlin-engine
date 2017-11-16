package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.linearmath.btMotionState
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.plugin.bullet.MotionState

/** Delegates get / set transform to the original motion state */
class BulletMotionState(ms: MotionState) : MotionState by ms, btMotionState() {
    companion object {
        val ID = GdxMatrix4()
    }

    val mat = Matrix4()

    override fun getWorldTransform(worldTrans: GdxMatrix4?) {
        worldTrans?.set(worldTransform)
    }

    override fun setWorldTransform(worldTrans: GdxMatrix4?) {
        mat.set(worldTrans ?: ID)
        worldTransform = mat
    }
}