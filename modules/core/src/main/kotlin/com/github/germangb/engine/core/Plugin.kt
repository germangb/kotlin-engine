package com.github.germangb.engine.core

/**
 * Does nothing...
 */
private fun TODO() = Unit

/**
 * Plugin interface
 */
interface Plugin {
    /**
     * Called before init
     */
    fun onPreInit() = TODO()

    /**
     * Called after init
     */
    fun onPostInit() = TODO()

    /**
     * Called before update
     */
    fun onPreUpdate() = TODO()

    /**
     * Called after update
     */
    fun onPostUpdate() = TODO()

    /**
     * Called before destruction
     */
    fun onPreDestroy() = TODO()

    /**
     * Called after destruction
     */
    fun onPostDestroy() = TODO()
}