package com.github.germangb.engine.backend.dektop.files

import com.github.germangb.engine.files.FileHandle
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * File handle for Desktop platform
 */
class DesktopFileHandle(private val file: File) : FileHandle {
    /**
     * Get file path
     */
    override val path = file.path

    /**
     * Check if user has read permissions
     */
    override val isReadable = file.canRead()

    /**
     * Check if user has write permissions
     */
    override val isWritable = file.canWrite()

    /**
     * Check if file is a directory
     */
    override val isDirectory = file.isDirectory

    /**
     * Parent parent
     */
    override val parent by lazy {
        DesktopFileHandle(file.parentFile)
    }

    /**
     * Get children
     */
    override val children by lazy {
        file.listFiles()
                .map { DesktopFileHandle(it) }
                .toTypedArray()
    }

    /**
     * Get a file input stream of the file
     */
    override fun read() = FileInputStream(file)

    /**
     * Get file output stream
     */
    override fun write() = FileOutputStream(file)
}