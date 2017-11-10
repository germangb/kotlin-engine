package com.github.germangb.renderer

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.utils.Destroyable
import com.github.germangb.player.Agent
import com.github.germangb.shooter.GameListener

/** Manage audio rendering */
class AudioRenderer(ctx: Context, private val assets: AssetManager) : GameListener, Destroyable {
    init {
        val birds = ctx.files.getLocal("audio/birds.ogg")
        assets.preloadAudio(birds, "ambient_birds", stream = true)
    }

    /** Initialize audio */
    fun init() {
        val birds = assets.getAudio("ambient_birds")
        birds?.play(loop = true)
    }

    /** Clear audio resources */
    override fun destroy() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSpawned(player: Agent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCrouched(player: Agent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onWalk(player: Agent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLook(player: Agent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPosition(player: Agent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTarget(player: Agent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}