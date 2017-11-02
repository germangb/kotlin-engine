package com.github.germangb.engine.framework.components

import com.github.germangb.engine.framework.Actor

/** Quick component */
class UpdateComponent(val update: () -> Unit)

/** Add update component */
fun Actor.addUpdate(upd: () -> Unit) = addComponent(UpdateComponent(upd))

/** Get updater component */
val Actor.updater get() = get(UpdateComponent::class)