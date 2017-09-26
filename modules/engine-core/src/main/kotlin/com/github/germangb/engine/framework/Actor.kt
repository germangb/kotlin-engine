package com.github.germangb.engine.framework

import kotlin.reflect.KClass

/**
 * Actors contain position information and a list of components
 */
class Actor internal constructor(val scene: Scene, private val pparent: Actor?) {
    init {
        // register actor in the framework
        //scene.iactors.add(this)
    }

    /**
     * String name given to this actor
     */
    var name: String = toString()

    /**
     * Parent actor
     */
    val parent get() = pparent

    /**
     * World & local transform
     */
    val transform = Transform()

    /**
     * Update components of the actors
     */
    private fun updateComponents() {
        icomponents.forEach {
            if(!it.init) {
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
    fun <T: Component> removeComponent(comp: KClass<T>) {
        icomponents.firstOrNull { it::class == comp }?.let {
            icomponents.remove(it)
        }
    }

    /**
     * Get a component by type
     */
    fun <T: Component> getComponent(comp: KClass<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return icomponents.firstOrNull { it::class == comp } as T?
    }

    /**
     * Get a component by type
     */
    inline fun <reified T: Component> getComponent() = getComponent(T::class)

    /**
     * In what order is this actor updated
     */
    var updateMode = UpdateMode.ROOT_FIRST

    /**
     * Update components of the actor and its descendants
     */
    internal fun update() {
        if(updateMode == UpdateMode.ROOT_FIRST) {
            updateComponents()
            ichildren.forEach {
                it.update()
            }
        } else {
            ichildren.forEach {
                it.update()
            }
            updateComponents()
        }
    }

    /**
     * Update transformations
     */
    internal fun updateTransforms() {
        if(pparent == null) {
            transform.iworld.set(transform.local)
        } else {
            transform.iworld.set(pparent.transform.iworld)
            transform.iworld.mul(transform.local)
        }

        children.forEach {
            it.updateTransforms()
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
    fun addChild(def: Actor.() -> Unit) {
        val actor = Actor(scene, this)
        def.invoke(actor)
        ichildren.add(actor)
    }

    /**
     * Find an actor by name
     */
    fun findActor(name: String): Actor? {
        if (this.name == name) return this
        children.forEach {
            val act = it.findActor(name)
            if (act != null) return act
        }
        return null
    }
}
