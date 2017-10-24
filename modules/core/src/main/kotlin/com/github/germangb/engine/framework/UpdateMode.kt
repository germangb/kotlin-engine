package com.github.germangb.engine.framework

enum class UpdateMode {
    /**
     * Depth-first traversal to update nodes
     */
    CHILDREN_FIRST,

    /**
     * Breadth-first traversal to update nodes
     */
    ROOT_FIRST
}
