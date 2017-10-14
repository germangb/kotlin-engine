package com.github.germangb.engine.core

/**
 * Does nothing...
 */
fun INFO(msg: String = "No info provided") = Unit

/**
 * Plugin interface
 */
interface Plugin {
    /**
     * Called before init
     */
    fun onPreInit() = INFO("This method is called before application is init")

    /**
     * Called after init
     */
    fun onPostInit() = INFO("This method is called after application is init")

    /**
     * Called before update
     */
    fun onPreUpdate() = INFO("This method is called before application is updated")

    /**
     * Called after update
     */
    fun onPostUpdate() = INFO("This method is called before application is updated")

    /**
     * Called before destruction
     */
    fun onPreDestroy() = INFO("This method is called before application is destroyed")

    /**
     * Called after destruction
     */
    fun onPostDestroy() = INFO("This method is called after application is destroyed")
}