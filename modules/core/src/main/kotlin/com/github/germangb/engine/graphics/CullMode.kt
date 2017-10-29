package com.github.germangb.engine.graphics

enum class CullMode {
    /**
     * Disable face culling
     */
    DISABLED,

    /**
     * Cull back faces
     */
    BACK,

    /**
     * Cull front faces
     */
    FRONT,

    /**
     * Cull front-back faces
     */
    FRONT_AND_BACK
}