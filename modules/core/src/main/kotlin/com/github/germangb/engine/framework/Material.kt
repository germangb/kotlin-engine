package com.github.germangb.engine.framework

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

    fun setTexture(key: String, value: Texture) {
        textures[key] = value
    }

    fun setFloat(key: String, value: Float) {
        floats[key] = value
    }

    fun setInt(key: String, value: Int) {
        ints[key] = value
    }

    fun setBoolean(key: String, value: Boolean) {
        bools[key] = value
    }
}