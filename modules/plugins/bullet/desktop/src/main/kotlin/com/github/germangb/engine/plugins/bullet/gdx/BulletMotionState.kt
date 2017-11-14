package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.linearmath.btMotionState
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.plugin.bullet.MotionState

class BulletMotionState(val ms: btMotionState) : MotionState {
    val auxGdx = GdxMatrix4()
    val aux = Matrix4()
    override var worldTransform: Matrix4c
        get() {
            ms.getWorldTransform(auxGdx)
            aux.set(auxGdx)
            return aux
        }
        set(value) {
            auxGdx.set(value)
            ms.setWorldTransform(auxGdx)
        }
}