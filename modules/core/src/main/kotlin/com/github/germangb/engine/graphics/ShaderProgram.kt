package com.github.germangb.engine.graphics

import com.github.germangb.engine.utils.Destroyable

private val MAX_UNIFORMS = 1024

/** Map shared among draw calls */
private val sharedUniformMap = LinkedHashMap<String, Any>(MAX_UNIFORMS)

/** Construct map of uniforms (the map is shared among all calls to ths function) */
fun uniformMap(vararg pairs: Pair<String, Any>): Map<String, Any> {
    sharedUniformMap.clear()
    return pairs.toMap(sharedUniformMap)
}

/**
 * Shader program
 */
interface ShaderProgram : Destroyable {
    /**
     * Vertex shader source
     */
    val vertex: String

    /**
     * Fragment shader source
     */
    val fragment: String
}
