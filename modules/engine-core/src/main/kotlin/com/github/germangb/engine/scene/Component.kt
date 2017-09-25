package com.github.germangb.engine.scene

abstract class Component {
    internal lateinit var iactor: Actor

    /**
     * Flag to tell if component has been init
     */
    internal var init = false

    /**
     * Actor with the component
     */
    val actor get() = iactor

    /**
     * Called when the actor is added to the scene
     */
    abstract fun init()

    /**
     * Called
     */
    abstract fun update()
}