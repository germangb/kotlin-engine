package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Matrix4c

/**
 * Compound physics shape
 */
interface CompoundPhysicsShape : PhysicsShape {
    /**
     * add shape bind compound shape
     */
    fun addChild(shape: PhysicsShape, local: Matrix4c)
}