package com.github.germangb.engine.animation

import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.Transform

class ActorAnimation(val root: Actor, val duration: Float, val timeline: Map<String, AnimationTimeline>) : Animation {
    var time = 0f
    val bones = mutableMapOf<String, Transform>()

    init {
        timeline.forEach { node, _ ->
            root.find(node)?.let {
                bones[node] = it.transform
            }
        }
    }

    fun update(dt: Float) {
        time += dt * 24
        if (time > duration) time = 0f

        timeline.forEach { node, timeline ->
            bones[node]?.let {
                timeline.applyTransform(time, it.local.identity())
            }
        }
    }

    override fun play() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}