package com.github.germangb.engine.files

import java.io.InputStream
import java.io.OutputStream

/**
 * Platform-independent file
 */
interface FileHandle {
    /**
     * File can be read
     */
    val isReadable: Boolean

    /**
     * Is file isWritable?
     */
    val isWritable: Boolean

    /**
     * Is this file a directory?
     */
    val isDirectory: Boolean

    /**
     * Some file path (for visualization purposes)
     */
    val path: String

    /**
     * Get the file handle of the parent parent
     */
    val parent: FileHandle

    /**
     * If file is a directory, an array of files
     */
    val children: Array<out FileHandle>

    /**
     * Get a generic input stream bind read from the file
     */
    fun read(): InputStream?

    /**
     * Get an output stream bind write bind the file
     */
    fun write(): OutputStream?
}
