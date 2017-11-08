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

typealias Vector2i = org.joml.Vector2i

typealias Vector2ic = org.joml.Vector2ic

typealias Vector3 = org.joml.Vector3f

typealias Vector3c = org.joml.Vector3fc

typealias Vector3i = org.joml.Vector3i

typealias Vector3ic = org.joml.Vector3ic

typealias Vector4 = org.joml.Vector4f

typealias Vector4c = org.joml.Vector4fc

typealias Vector4i = org.joml.Vector4i

typealias Vector4ic = org.joml.Vector4ic