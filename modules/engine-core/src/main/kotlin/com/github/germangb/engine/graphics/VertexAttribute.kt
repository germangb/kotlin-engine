package com.github.germangb.engine.graphics

import com.github.germangb.engine.graphics.VertexAttributeType.*

enum class VertexAttributeType(val bytes: Int) { FLOAT(4), INT(4), SHORT(2), BYTE(1) }

enum class VertexAttribute(val size: Int, val type: VertexAttributeType) {
    /**
     * 3D position
     */
    POSITION(3, FLOAT),

    /**
     * 2D position
     */
    POSITION2(2, FLOAT),

    /**
     * 3D normal
     */
    NORMAL(3, FLOAT),

    /**
     * Tex coordinate
     */
    UV(2, FLOAT),

    /**
     * polygon id
     */
    ID(1, FLOAT),

    /** RGB color */
    COLOR_RGB(3, FLOAT),

    /** RGBA color */
    COLOR_RGBA(4, FLOAT),

    /**
     * Joint ID
     */
    JOINT_IDS(4, FLOAT),

    /**
     * Joint weights
     */
    JOINT_WEIGHTS(4, FLOAT)
}