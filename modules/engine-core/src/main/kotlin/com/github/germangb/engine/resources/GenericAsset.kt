package com.github.germangb.engine.resources

abstract class GenericAsset<out T: Any>(val manager: AssetManager) {
    /**
     * Get resource or null if it is not loaded
     */
    abstract val resource: T?
}