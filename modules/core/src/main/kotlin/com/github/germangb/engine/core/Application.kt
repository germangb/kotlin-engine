package com.github.germangb.engine.core

interface Application : Destroyable {
    /**
     * Called to init application
     */
    fun init()

    /**
     * Called once per itime
     */
    fun update()
}