package com.github.germangb.engine.framework

abstract class Component {
    internal lateinit var iactor: GameActor

    /**
     * Flag to tell if component has been init
     */
    internal var init = false

    /**
     * GameActor with the component
     */
    val actor get() = iactor

    /**
     * Called when the actor is added to the framework
     */
    abstract fun init()

    /**
     * Called
     */
    abstract fun update()

    /**
     * Message received
     */
    abstract fun receive(message: Any, callback: (Any) -> Unit)
}