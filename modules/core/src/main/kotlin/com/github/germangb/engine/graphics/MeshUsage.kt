package com.github.germangb.engine.graphics

/**
 * Mesh usage hint
 */
enum class MeshUsage {
    /** Mesh never changes */
    STATIC,

    /** Mesh might change */
    DYNAMIC,

    /** Mesh changes every frame */
    STREAM
}