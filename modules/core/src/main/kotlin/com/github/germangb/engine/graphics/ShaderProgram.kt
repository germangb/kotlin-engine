package com.github.germangb.engine.graphics

import com.github.germangb.engine.utils.Destroyable

/**
 * Map shader uniforms to properties
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Uniform(val name: String)

/**
 * Shader program
 */
interface ShaderProgram<T> : Destroyable
