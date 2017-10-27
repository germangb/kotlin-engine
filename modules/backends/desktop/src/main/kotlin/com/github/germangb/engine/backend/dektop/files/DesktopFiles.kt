package com.github.germangb.engine.backend.dektop.files

import com.github.germangb.engine.files.Files
import java.io.File

/**
 * Files from desktop platform
 */
class DesktopFiles : Files {
    /**
     * Get reference bind local file
     */
    override fun getLocal(path: String) = DesktopFileHandle(File(path))
}