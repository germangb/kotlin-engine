package com.github.germangb.engine.graphics

import com.github.germangb.engine.math.*

interface Uniforms {
    /**
     * Bind an integer
     */
    infix fun Int.bindsTo(name: String)

    /**
     * Bind an float
     */
    infix fun Float.bindsTo(name: String)

    /**
     * Bind a texture
     */
    infix fun Texture.bindsTo(name: String)

    /**
     * Bind a vec2
     */
    infix fun Vector2c.bindsTo(name: String)

    /**
     * Bind a vec3
     */
    infix fun Vector3c.bindsTo(name: String)

    /**
     * Bind a vec4
     */
    infix fun Vector4c.bindsTo(name: String)

    /**
     * Bind a mat3
     */
    infix fun Matrix3c.bindsTo(name: String)

    /**
     * Bind a mat4
     */
    infix fun Matrix4c.bindsTo(name: String)

    /**
     * Bind a mat4 array
     */
    infix fun Array<Matrix4c>.bindsTo(name: String)

    /**
     * Bind a mat3 array
     */
    infix fun Array<Matrix3c>.bindsTo(name: String)

    /**
     * Bind a vec4 array
     */
    infix fun Array<Vector4c>.bindsTo(name: String)

    /**
     * Bind a vec3 array
     */
    infix fun Array<Vector3c>.bindsTo(name: String)

    /**
     * Bind a vec2 array
     */
    infix fun Array<Vector2c>.bindsTo(name: String)
}
