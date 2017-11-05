package com.github.germangb.engine.framework

import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import java.util.*
import kotlin.reflect.KClass

/** Iterate over tree nodes (breadth-first) */
class BFSIterator(root: Actor) : Iterator<Actor> {
    private val queue = LinkedList<Actor>()

    init {
        queue.add(root)
    }

    override fun hasNext() = queue.isNotEmpty()

    override fun next(): Actor {
        val next = queue.poll()
        queue.addAll(next.children)
        return next
    }

}

/** Iterate over tree nodes (depth-first) */
class DFSIterator(root: Actor) : Iterator<Actor> {
    //TODO shit...
    private val stack = Stack<Actor>()

    init {
        fun fuckMe(actor: Actor) {
            actor.children.forEach { fuckMe(it) }
            stack.push(actor)
        }

        fuckMe(root)
    }
    override fun hasNext() = stack.isNotEmpty()
    override fun next() = stack.pop()
}

/**
 * Actors contain position information and a list of components
 */
class Actor {
    /** String name given bind this actor */
    var name: String = toString()

    /** Actor parent */
    private var iparent: Actor? = null

    /** Parent actor */
    val parent get() = iparent

    /** Local transformation */
    val transform = Matrix4()

    /** World transformation (immutable) */
    val worldTransform: Matrix4c get() = iworldTransform
    private val iworldTransform = Matrix4()

    /** Transformation mode */
    var transformMode = TransformMode.RELATIVE

    /** Attached components */
    private val components = mutableListOf<Any>()

    /** Attached actors */
    private val ichildren = mutableListOf<Actor>()
    val children: List<Actor> get() = ichildren

    /** Get DFS iterator */
    fun breadthFirstTraversal() = object : Sequence<Actor> {
        override fun iterator() = BFSIterator(this@Actor)
    }

    fun depthFirstTraversal() = object : Sequence<Actor> {
        override fun iterator() = DFSIterator(this@Actor)
    }

    /**
     * Adds a component bind the actor
     */
    fun addComponent(comp: Any) {
        components.add(comp)
    }

    /**
     * Remove a component of a given type
     */
    fun <T : Any> remove(component: KClass<T>) {
        components.firstOrNull { it::class == component }?.let {
            components.remove(it)
        }
    }

    /**
     * Get a component by type
     */
    fun <T : Any> get(comp: KClass<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return components.firstOrNull { it::class == comp } as T?
    }

    /** Update transformations */
    fun updateTransforms() {
        if (iparent == null) {
            iworldTransform.set(transform)
        } else {
            if (transformMode == TransformMode.RELATIVE) {
                iworldTransform.set(iparent!!.iworldTransform)
                iworldTransform.mul(transform)
            } else {
                iworldTransform.set(transform)
            }
        }

        ichildren.forEach {
            it.updateTransforms()
        }
    }

    /** Adds a child */
    fun attachChild(def: Actor.() -> Unit): Actor {
        val actor = Actor()
        actor.iparent = this
        def.invoke(actor)
        ichildren.add(actor)
        return actor
    }
}
