package com.github.germangb.engine.framework.materials

import com.github.germangb.engine.graphics.Texture

/** Access diffuse map in material */
val DIFFUSE_MAP_KEY = "diffuse_map"

/**
 * Diffuse texture
 */
var Materialc.diffuse: Texture
    get() = getTexture(DIFFUSE_MAP_KEY)
    set(value) = (this as Material).setTexture(DIFFUSE_MAP_KEY, value)
