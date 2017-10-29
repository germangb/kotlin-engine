package com.github.germangb.engine.graphics

import com.github.germangb.engine.graphics.VertexAttributeType.*

/** Attribute data types */
enum class VertexAttributeType(val bytes: Int) { FLOAT(4), INT(4), SHORT(2), BYTE(1) }

/** Instance attributes */
enum class InstanceAttribute(val size: Int, val type: VertexAttributeType) {
    /** Transform attribute */
    TRANSFORM(16, FLOAT),

    /** Texture coordinate offset */
    UV_OFFSET(2, FLOAT),

    /** RGB color */
    COLOR_RGB(3, FLOAT),

    /** RGBA color */
    COLOR_RGBA(4, FLOAT)
}

/** Vertex attributes */
enum class VertexAttribute(val size: Int, val type: VertexAttributeType) {
    /** 3D position */
    POSITION(3, FLOAT),

    /** 2D position */
    POSITION2(2, FLOAT),

    /** 3D normal */
    NORMAL(3, FLOAT),

    /** Tex coordinate */
    UV(2, FLOAT),

    /** polygon id */
    ID(1, FLOAT),

    /** RGB color */
    COLOR_RGB(3, FLOAT),

    /** RGBA color */
    COLOR_RGBA(4, FLOAT),

    /** Joint ID */
    JOINT_IDS(4, FLOAT),

    /** Joint weights */
    JOINT_WEIGHTS(4, FLOAT)
}