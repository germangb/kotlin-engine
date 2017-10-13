package com.github.germangb.engine.core

/**
 * Plugin interface
 */
interface Plugin {
    /**
     * Called before init
     */
    fun onPreInit()

    /**
     * Called after init
     */
    fun onPostInit()

    /**
     * Called before update
     */
    fun onPreUpdate()

    /**
     * Called after update
     */
    fun onPostUpdate()

    /**
     * Called before destruction
     */
    fun onPreDestroy()

    /**
     * Called after destruction
     */
    fun onPostDestroy()
}