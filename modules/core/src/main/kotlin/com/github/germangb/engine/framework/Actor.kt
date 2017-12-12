package com.github.germangb.engine.framework

import com.github.germangb.engine.framework.TransformMode.RELATIVE
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import kotlin.reflect.KClass

/**
 * Actors contain position information and a list of components
 */
class Actor {
    /** String name given bind this actor */
    var name: String = toString()

    /** Actor parent */
    private var iparent: Actor? = null

    /** Parent actor */
    val parent get() = iparent ?: this

    /** Local transformation */
    val transform = Matrix4()

    /** World transformation */
    val worldTransform: Matrix4c = Matrix4()

    /** Transformation mode */
    var transformMode = RELATIVE

    /** Attached components */
    private val components = mutableMapOf<KClass<*>, Any>()

    val children: List<Actor> = mutableListOf()

    /** Get BFS iterator */
    fun breadthFirstTraversal() = object : Sequence<Actor> {
        override fun iterator() = BFSIterator(this@Actor)
    }

    /** Get DFS iterator */
    fun depthFirstTraversal() = object : Sequence<Actor> {
        override fun iterator() = DFSIterator(this@Actor)
    }

    /**
     * Adds a component bind the actor
     */
    fun addComponent(comp: Any) {
        components[comp::class] = comp
    }

    /**
     * Remove a component of a given type
     */
    fun <T : Any> remove(component: KClass<T>) {
        components.remove(component)
    }

    /**
     * Get a component by type
     */
    fun <T : Any> get(comp: KClass<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return components[comp] as T?
    }

    /** Update transformations */
    fun computeTransforms() {
        val w = worldTransform as Matrix4
        if (iparent == null) {
            w.set(transform)
        } else {
            if (transformMode == RELATIVE) {
                w.set(iparent!!.worldTransform)
                w.mul(transform)
            } else {
                worldTransform.set(transform)
            }
        }

        children.forEach {
            it.computeTransforms()
        }
    }

    /** Adds a child */
    fun attachChild(def: Actor.() -> Unit): Actor {
        val actor = Actor()
        actor.iparent = this
        def.invoke(actor)
        (children as MutableList).add(actor)
        return actor
    }
}
