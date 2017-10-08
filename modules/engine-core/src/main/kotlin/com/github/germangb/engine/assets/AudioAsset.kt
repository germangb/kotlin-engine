package com.github.germangb.engine.assets

import com.github.germangb.engine.audio.Audio

/**
 * Manager audio resource
 */
class AudioAsset(manager: AssetManager, val path: String) : GenericAsset<Audio>(manager) {
    init {
       manager.loadAudio(path)
    }

    /**
     * Get resource
     */
    override val resource get() = manager.getAudio(path)
}