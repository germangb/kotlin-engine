package com.github.germangb.engine.graphics

enum class VertexAttribute(val size: Int) {
    /**
     * 3D position
     */
    POSITION(3),

    /**
     * 2D position
     */
    POSITION2(2),

    /**
     * 3D normal
     */
    NORMAL(3),

    /**
     * Texture coordinate
     */
    UV(2)
}