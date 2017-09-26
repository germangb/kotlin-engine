package com.github.germangb.engine.framework

import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Vector3c

class Camera {
    /**
     * Projection matrix
     */
    private val _projection = Matrix4()

    /** Projection matrix */
    val projection: Matrix4c get() = _projection

    /**
     * View transformation matrix
     */
    private val _view = Matrix4()

    /** View transformation matrix */
    val view: Matrix4c get() = _view

    /**
     * Set perspective projection
     */
    fun setPerspective(fovy: Float, aspect: Float, zNear: Float, zFar: Float) = _projection.setPerspective(fovy, aspect, zNear, zFar)

    /**
     * Set view transform
     */
    fun setLookAt(eye: Vector3c, center: Vector3c, up: Vector3c) = _view.setLookAt(eye, center, up)
}