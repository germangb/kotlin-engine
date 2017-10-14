package com.github.germangb.engine.animation

import com.github.germangb.engine.math.Quaternionc
import com.github.germangb.engine.math.Vector3c

/** Animation key */
sealed class GenericKey<out T>(val time: Float, val key: T)

/** Rotation keys */
class RotationKey(time: Float, key: Quaternionc) : GenericKey<Quaternionc>(time, key)

/** Position keys */
class PositionKey(time: Float, key: Vector3c) : GenericKey<Vector3c>(time, key)

/** Scale keys */
class ScaleKey(time: Float, key: Vector3c) : GenericKey<Vector3c>(time, key)
