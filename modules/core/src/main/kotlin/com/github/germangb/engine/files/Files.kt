package com.github.germangb.engine.files

/**
 * API for interacting with files
 */
interface Files {
    /**
     * Get a local file from the application
     */
    fun getLocal(path: String): FileHandle
}