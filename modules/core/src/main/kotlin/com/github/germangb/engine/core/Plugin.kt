package com.github.germangb.engine.core

/**
 * Does nothing...
 */
fun _TODO() = Unit

/**
 * Plugin interface
 */
interface Plugin {
    /**
     * Called before init
     */
    fun onPreInit() = _TODO()

    /**
     * Called after init
     */
    fun onPostInit() = _TODO()

    /**
     * Called before update
     */
    fun onPreUpdate() = _TODO()

    /**
     * Called after update
     */
    fun onPostUpdate() = _TODO()

    /**
     * Called before destruction
     */
    fun onPreDestroy() = _TODO()

    /**
     * Called after destruction
     */
    fun onPostDestroy() = _TODO()
}