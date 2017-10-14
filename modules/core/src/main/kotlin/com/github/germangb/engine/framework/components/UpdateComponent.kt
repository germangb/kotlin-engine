package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component

/**
 * Quick component
 */
class UpdateComponent(val upd: () -> Unit) : Component() {
    override fun init() = Unit
    override fun update() = upd.invoke()
    override fun receive(message: Any, callback: (Any) -> Unit) = Unit
}

/**
 * Add update component
 */
fun Actor.addUpdate(upd: () -> Unit) = addComponent(UpdateComponent(upd))