package com.github.germangb.engine.framework

import java.util.*

internal class BFSIterator(root: Actor) : Iterator<Actor> {
    val queue = LinkedList<Actor>()

    init {
        fun add(actor: Actor) {
            queue.add(actor)
            actor.children.forEach { add(it) }
        }

        add(root)
    }

    override fun hasNext() = queue.isNotEmpty()

    override fun next(): Actor {
        val next = queue.poll()
        return next
    }

}

internal class DFSIterator(root: Actor) : Iterator<Actor> {
    val stack = Stack<Actor>()

    init {
        fun push(actor: Actor) {
            actor.children.forEach { push(it) }
            stack.push(actor)
        }

        push(root)
    }

    override fun hasNext() = stack.isNotEmpty()
    override fun next() = stack.pop()
}

