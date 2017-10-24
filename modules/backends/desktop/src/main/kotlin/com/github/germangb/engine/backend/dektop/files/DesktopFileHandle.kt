package com.github.germangb.engine.backend.dektop.files

import com.github.germangb.engine.files.FileHandle
import java.io.BufferedInputStream
import java.io.FileInputStream

/**
 * File handle for Desktop platform
 */
class DesktopFileHandle(val path: String) : FileHandle {
    /**
     * Get a file input stream of the file
     */
    override fun read() = FileInputStream(path)

    /**
     * Decorated file input stream
     */
    override fun readBuffered(bufferSize: Int) = BufferedInputStream(FileInputStream(path), bufferSize)

}