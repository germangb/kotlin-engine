package com.github.germangb.engine.framework.materials

import com.github.germangb.engine.graphics.Texture

/**
 * Rendering properties
 */
abstract class Material {
    /**
     * Material textures
     */
    private val itextures = mutableMapOf<String, Texture>()

    /**
     * Material floats
     */
    private val ifloats = mutableMapOf<String, Float>()

    /**
     * Material ints
     */
    private val iints = mutableMapOf<String, Int>()

    /**
     * Material booleans
     */
    private val ibools = mutableMapOf<String, Boolean>()

    /**
     * Stored textures
     */
    val textures: Map<String, Texture> = itextures

    /**
     * Stored floats
     */
    val floats: Map<String, Float> = ifloats

    /**
     * Stored ints
     */
    val ints: Map<String, Int> = iints

    /**
     * Stored booleand
     */
    val bools: Map<String, Boolean> = ibools

    /**
     * Set texture parameter
     */
    operator fun set(key: String, texture: Texture) {
        itextures[key] = texture
    }

    /**
     * Set Float parameter
     */
    operator fun set(key: String, float: Float) {
        ifloats[key] = float
    }

    /**
     * Set Int parameter
     */
    operator fun set(key: String, int: Int) {
        iints[key] = int
    }

    /**
     * Set Boolean parameter
     */
    operator fun set(key: String, bool: Boolean) {
        ibools[key] = bool
    }
}
