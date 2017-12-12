package com.github.germangb.engine.graphics

import com.github.germangb.engine.graphics.VertexAttributeType.*

/** Attribute data types */
enum class VertexAttributeType(val bytes: Int) { FLOAT(4), INT(4), SHORT(2), BYTE(1) }

/**
 * Per-instance attribute
 */
enum class InstanceAttribute(val size: Int, val type: VertexAttributeType) {
    /** General purpose transform */
    TRANSFORM(16, FLOAT),

    /** Texture coordinate offset */
    UV(2, FLOAT),

    /** RGB color offset */
    COLOR3(3, FLOAT),

    /** RGBA color offset */
    COLOR4(4, FLOAT)
}

/**
 * Per-vertex attributes
 */
enum class VertexAttribute(val size: Int, val type: VertexAttributeType) {
    /** 4D position */
    POSITION4(4, FLOAT),

    /** 3D position */
    POSITION3(3, FLOAT),

    /** 2D position */
    POSITION2(2, FLOAT),

    /** 3D normal */
    NORMAL(3, FLOAT),

    /** Tex coordinate */
    UV(2, FLOAT),

    /** polygon id */
    ID(1, FLOAT),

    /** RGB color */
    COLOR3(3, FLOAT),

    /** RGBA color */
    COLOR4(4, FLOAT),

    /** Joint ID */
    JOINT_IDS(4, FLOAT),

    /** Joint weights */
    JOINT_WEIGHTS(4, FLOAT)
}