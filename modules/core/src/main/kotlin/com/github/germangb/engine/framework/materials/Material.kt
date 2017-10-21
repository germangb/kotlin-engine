package com.github.germangb.engine.framework.materials

import com.github.germangb.engine.assets.utils.DummyTexture
import com.github.germangb.engine.graphics.Texture

/**
 * Collection of properties
 */
class Material : Materialc {
    private val textures = mutableMapOf<String, Texture>()
    private val floats = mutableMapOf<String, Float>()
    private val ints = mutableMapOf<String, Int>()
    private val bools = mutableMapOf<String, Boolean>()

    override fun getTexture(key: String) = textures[key] ?: DummyTexture
    override fun getFloat(key: String) = floats[key] ?: 0f
    override fun getInt(key: String) = ints[key] ?: 0
    override fun getBoolean(key: String) = bools[key] ?: false

    /**
     * Set material texture property
     */
    fun setTexture(key: String, value: Texture) {
        textures[key] = value
    }

    /**
     * Set material float property
     */
    fun setFloat(key: String, value: Float) {
        floats[key] = value
    }

    /**
     * Set material int property
     */
    fun setInt(key: String, value: Int) {
        ints[key] = value
    }

    /**
     * Set material boolean property
     */
    protected fun setBoolean(key: String, value: Boolean) {
        bools[key] = value
    }
}