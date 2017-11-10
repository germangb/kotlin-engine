package com.github.germangb.engine.math

typealias FrustumIntersection = org.joml.FrustumIntersection

typealias Interpolation = org.joml.Interpolationf

typealias Math = org.joml.Math

/** PI float32 */
val PI = Math.PI.toFloat()

/** Two times PI */
val PI2 = PI*2

/** Convert from degrees to radians */
fun toRadians(deg: Float) = deg * PI / 180

/** Convert from radians to degrees */
fun toDegrees(rad: Float) = rad * 180 / PI

/** exp float32 */
fun exp(n: Float) = Math.exp(n.toDouble()).toFloat()

/** Cos for float32 */
fun cos(n: Float) = Math.cos(n.toDouble()).toFloat()

/** Sin for float32 */
fun sin(n: Float) = Math.sin(n.toDouble()).toFloat()

/** tan for float32 */
fun tan(n: Float) = Math.tan(n.toDouble()).toFloat()

/** atan2 for float32 */
fun atan2(n: Float, m: Float) = Math.atan2(n.toDouble(), m.toDouble()).toFloat()

typealias AABB = org.joml.AABBf

/** Missing AABB test function */
fun FrustumIntersection.testAab(aab: AABB) = testAab(aab.minX, aab.minY, aab.minZ, aab.maxX, aab.maxY, aab.maxZ)

typealias Matrix3 = org.joml.Matrix3f

typealias Matrix3c = org.joml.Matrix3fc

typealias Matrix3x2 = org.joml.Matrix3x2f

typealias Matrix3x2c = org.joml.Matrix3x2fc

typealias Matrix4 = org.joml.Matrix4f

typealias Matrix4c = org.joml.Matrix4fc

typealias Matrix4x3 = org.joml.Matrix4x3f

typealias Matrix4x3c = org.joml.Matrix4x3fc

typealias Quaternion = org.joml.Quaternionf

typealias QuaternionInterpolator = org.joml.QuaternionfInterpolator

typealias Quaternionc = org.joml.Quaternionfc

typealias Vector2 = org.joml.Vector2f

typealias Vector2c = org.joml.Vector2fc

val Vector2c.x get() = x()

val Vector2c.y get() = y()

typealias Vector2i = org.joml.Vector2i

typealias Vector2ic = org.joml.Vector2ic

val Vector2ic.x get() = x()

val Vector2ic.y get() = y()

typealias Vector3 = org.joml.Vector3f

typealias Vector3c = org.joml.Vector3fc

val Vector3c.x get() = x()

val Vector3c.y get() = y()

val Vector3c.z get() = z()

typealias Vector3i = org.joml.Vector3i

typealias Vector3ic = org.joml.Vector3ic

val Vector3ic.x get() = x()

val Vector3ic.y get() = y()

val Vector3ic.z get() = z()

typealias Vector4 = org.joml.Vector4f

typealias Vector4c = org.joml.Vector4fc

val Vector4c.x get() = x()

val Vector4c.y get() = y()

val Vector4c.z get() = z()

val Vector4c.w get() = w()

typealias Vector4i = org.joml.Vector4i

typealias Vector4ic = org.joml.Vector4ic

val Vector4ic.x get() = x()

val Vector4ic.y get() = y()

val Vector4ic.z get() = z()

val Vector4ic.w get() = w()
