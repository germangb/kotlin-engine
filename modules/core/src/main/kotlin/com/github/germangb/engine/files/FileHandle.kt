package com.github.germangb.engine.files

import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

private val buffer by lazy {
    ByteArray(DEFAULT_BUFFER_SIZE)
}

/** Read entire binary file */
fun FileHandle.read(output: ByteBuffer) {
    inputStream.use {
        var read = it?.read(buffer) ?: return
        while (read > 0) {
            output.put(buffer, 0, read)
            read = it.read(buffer)
        }
    }
}

/** Read entire file as text */
fun FileHandle.readText(fallback: String = "") = inputStream?.use {
    it.bufferedReader().readText()
} ?: fallback

/**
 * Platform-independent file
 */
interface FileHandle {
    /** File can be asInput */
    val isReadable: Boolean

    /** Is file isWritable? */
    val isWritable: Boolean

    /** Is this file a directory? */
    val isDirectory: Boolean

    /** Some file path (for visualization purposes) */
    val path: String

    /** Get the file handle of the parent file */
    val parent: FileHandle

    /** If file is a directory, an array of files */
    val children: Array<out FileHandle>

    /** Get input stream */
    val inputStream: InputStream?

    /** Get output stream */
    val outputStream: OutputStream?
}
