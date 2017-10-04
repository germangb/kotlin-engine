package com.github.germangb.engine.backend.lwjgl.fonts

import com.github.germangb.engine.fonts.Font
import com.github.germangb.engine.graphics.Texture
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype.*

class STBTTFont(override val texture: Texture, val chars: STBTTPackedchar.Buffer) : Font {
    /**
     * Free native resources
     */
    override fun destroy() {
        texture.destroy()
        chars.free()
    }
}