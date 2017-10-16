package com.github.germangb.engine.framework

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
     * Called when the actor is added to the framework
     */
    open fun init() = Unit

    /**
     * Called
     */
    open fun update() = Unit
}