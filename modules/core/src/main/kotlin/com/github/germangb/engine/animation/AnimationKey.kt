package com.github.germangb.engine.animation

import com.github.germangb.engine.math.Quaternionc
import com.github.germangb.engine.math.Vector3c

/** Animation key */
sealed class GenericKey<out T>(open val time: Float, open val key: T)

/** Rotation keys */
data class RotationKey(override val time: Float, override val key: Quaternionc) : GenericKey<Quaternionc>(time, key)

/** Position keys */
data class PositionKey(override val time: Float, override val key: Vector3c) : GenericKey<Vector3c>(time, key)

/** Scale keys */
data class ScaleKey(override val time: Float, override val key: Vector3c) : GenericKey<Vector3c>(time, key)
