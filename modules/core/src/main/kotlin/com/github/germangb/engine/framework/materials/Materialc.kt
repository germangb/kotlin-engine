package com.github.germangb.engine.framework.materials

import com.github.germangb.engine.graphics.Texture

interface Materialc {
    /**
     * Material texture property
     */
    fun getTexture(key: String): Texture

    /**
     * Material float property
     */
    fun getFloat(key: String): Float

    /**
     * Material int property
     */
    fun getInt(key: String): Int

    /**
     * Material bool property
     */
    fun getBoolean(key: String): Boolean
}