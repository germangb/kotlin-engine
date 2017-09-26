package com.github.germangb.engine.framework

import java.util.*

/**
 * Contains a hierarchical representation of the framework and a camera
 */
class Scene {
    /**
     * List of actors in the framework
     */
    private val iactors = mutableListOf<Actor>()

    /**
     * Get list of currelty active actors
     */
    val actors: List<Actor> get() = iactors

    /**
     * Root node of the framework
     */
    val root = Actor(this, null)

    /**
     * Scene camera
     */
    val camera = Camera()

    /**
     * Update actors
     */
    fun update() {
        root.update()
        root.updateTransforms()
        computeActiveActors()
    }

    /**
     * Compute active actors
     */
    private fun computeActiveActors() {
        iactors.clear()
        val stack = Stack<Actor>()
        stack.add(root)
        val actors = mutableListOf<Actor>()
        while (stack.isNotEmpty()) {
            val actor = stack.pop()
            iactors.add(actor)
            actor.children.forEach {
                stack.add(it)
            }
        }
    }

    /** Return a pretty tree */
    fun prettyPrint() = buildString {
        val auxStack = Stack<Pair<Actor, String>>()
        auxStack.push(Pair(root, ""))

        while (auxStack.isNotEmpty()) {
            val act = auxStack.pop()
            append("${act.second}* ${act.first.name}")

            // components
            act.first.components.forEach {
                append("\n${act.second}  | $it")
            }

            val offset = act.second+"  "
            act.first.children.forEach {
                auxStack.push(Pair(it, offset))
            }

            if (auxStack.isNotEmpty()) {
                append("\n")
            }
        }
    }
}

