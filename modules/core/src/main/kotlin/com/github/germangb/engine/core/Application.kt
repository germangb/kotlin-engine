package com.github.germangb.engine.core

import com.github.germangb.engine.utils.Destroyable

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