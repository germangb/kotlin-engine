package com.github.germangb.engine.files

import java.io.BufferedInputStream
import java.io.InputStream

/**
 * Platform-independent file
 */
interface FileHandle {
    /**
     * Some file path (for visualization purposes)
     */
    val path: String

    /**
     * Get a generic input stream to read from the file
     */
    fun read(): InputStream

    /**
     * Get a buffered input stream to read from the file
     */
    fun readBuffered(bufferSize: Int): BufferedInputStream
}