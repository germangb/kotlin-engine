package com.github.germangb.engine.core

interface Application {
    /**
     * Called to init application
     */
    fun init()

    /**
     * Called once per frame
     */
    fun update()
}