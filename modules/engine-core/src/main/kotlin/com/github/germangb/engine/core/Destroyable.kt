package com.github.germangb.engine.core

interface Destroyable {
    /**
     * Free native resources
     */
    fun destroy()
}