package com.github.germangb.engine.backend.lwjgl.files

import com.github.germangb.engine.files.Files

/**
 * Files from desktop platform
 */
class DesktopFiles : Files {
    /**
     * Get reference to local file
     */
    override fun getLocal(path: String) = DesktopFileHandle(path)
}