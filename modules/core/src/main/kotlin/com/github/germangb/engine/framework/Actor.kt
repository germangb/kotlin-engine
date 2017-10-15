package com.github.germangb.engine.framework

import kotlin.reflect.KClass

/**
 * Actors contain position information and a list of components
 */
class Actor {
    /**
     * String name given to this actor
     */
    var name: String = toString()

    /** Actor parent */
    private var iparent: Actor? = null

    /** Root scene */
    private var iroot: Actor = this

    /**
     * Parent actor
     */
    val parent get() = iparent

    /** Root actor */
    val root get() = iroot

    /**
     * World & local transform
     */
    val transform = Transform()

    /**
     * Transformation mode
     */
    var transformMode = TransformMode.RELATIVE

    /**
     * Update components of the actors
     */
    private fun updateInternalComponents() {
        icomponents.forEach {
            if (!it.init) {
                it.init = true
                it.init()
            }
            it.update()
        }
    }

    /**
     * Attached components
     */
    private val icomponents = mutableListOf<Component>()

    /**
     * Attached components
     */
    val components: List<Component> get() = icomponents

    /**
     * Adds a component to the actor
     */
    fun addComponent(comp: Component) {
        try {
            // trigger exception if component is fresh
            comp.actor.toString()
            throw Exception("Component instance cannot be reused")
        } catch (e: Exception) {
            comp.iactor = this
            icomponents.add(comp)
        }
    }

    /**
     * Remove a component of a given type
     */
    fun <T : Component> removeComponent(comp: KClass<T>) {
        icomponents.firstOrNull { it::class == comp }?.let {
            icomponents.remove(it)
        }
    }

    /**
     * Get a component by type
     */
    fun <T : Component> getComponent(comp: KClass<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return icomponents.firstOrNull { it::class == comp } as T?
    }

    /**
     * Get a component by type
     */
    inline fun <reified T : Component> getComponent() = getComponent(T::class)

    /**
     * Message mode
     */
    var messageMode = MessageMode.READ_SEND

    /**
     * Send a message to the actor
     */
    fun send(message: Any, callback: (Any) -> Unit = {}) {
        if (messageMode.read) {
            components.forEach {
                it.receive(message, callback)
            }
        }

        // pass message up the hierarchy
        if (messageMode.send) {
            children.forEach {
                it.send(message, callback)
            }
        }
    }

    /**
     * In what order is this actor updated
     */
    var updateMode = UpdateMode.ROOT_FIRST

    /**
     * Update components of the actor and its descendants
     */
    private fun updateComponents() {
        if (updateMode == UpdateMode.ROOT_FIRST) {
            updateInternalComponents()
            ichildren.forEach {
                it.updateComponents()
            }
        } else {
            ichildren.forEach {
                it.updateComponents()
            }
            updateInternalComponents()
        }
    }

    /**
     * Update actors
     */
    fun update() {
        computeTransforms()
        updateComponents()
    }

    /**
     * Update transformations
     */
    private fun computeTransforms() {
        if (this == root) {
            transform.iworld.set(transform.local)
        } else {
            if (transformMode == TransformMode.RELATIVE) {
                transform.iworld.set(iparent!!.transform.iworld)
                transform.iworld.mul(transform.local)
            } else {
                transform.iworld.set(transform.local)
            }
        }

        ichildren.forEach {
            it.computeTransforms()
        }
    }

    /**
     * Attached actors
     */
    private val ichildren = mutableListOf<Actor>()

    /**
     * Attached actors
     */
    val children: List<Actor> get() = ichildren

    /**
     * Adds a child
     */
    fun addChild(def: Actor.() -> Unit): Actor {
        val actor = Actor()
        actor.iparent = this
        actor.iroot = root
        def.invoke(actor)
        ichildren.add(actor)
        return actor
    }

    /**
     * Find an actor by name
     */
    fun find(name: String): Actor? {
        if (this.name == name) {
            return this
        }
        children.forEach {
            val act = it.find(name)
            if (act != null) return act
        }
        return null
    }
}
