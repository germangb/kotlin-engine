package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Component

/**
 * Quick component
 */
class UpdateComponent(val update: () -> Unit) : Component() {
    override fun update() = update.invoke()
}

/**
 * Add update component
 */
fun Actor.addUpdate(upd: () -> Unit) = addComponent(UpdateComponent(upd))