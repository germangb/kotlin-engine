package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Matrix4c

interface MotionState {
    /** Get/set world transform */
    var worldTransform: Matrix4c
}