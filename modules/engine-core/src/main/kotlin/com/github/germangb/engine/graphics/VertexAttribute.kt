package com.github.germangb.engine.graphics

import com.github.germangb.engine.graphics.VertexAttributeType.*

enum class VertexAttributeType { FLOAT, INT, SHORT, BYTE }

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
    ID(1, INT),

    /**
     * Joint ID
     */
    JOINT_ID(4, INT),

    /**
     * Joint weights
     */
    JOINT_WEIGHTS(4, FLOAT)
}