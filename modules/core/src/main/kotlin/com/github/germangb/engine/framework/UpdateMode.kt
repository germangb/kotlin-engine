package com.github.germangb.engine.framework

enum class UpdateMode {
    /**
     * Update the children actor first (DFS)
     */
    CHILDREN_FIRST,

    /**
     * Update the root first (BFS)
     */
    ROOT_FIRST
}
