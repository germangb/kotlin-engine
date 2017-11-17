package com.github.germangb.engine.backend.dektop.core

interface DesktopModule {
    companion object {
        fun TODO() = Unit
    }

    /** Called before init */
    fun onPreInit() = TODO()

    /** Called after init */
    fun onPostInit() = TODO()

    /** Called before update */
    fun onPreUpdate() = TODO()

    /** Called after update */
    fun onPostUpdate() = TODO()

    /** Called before destruction */
    fun onPreDestroy() = TODO()

    /** Called after destruction */
    fun onPostDestroy() = TODO()
}
