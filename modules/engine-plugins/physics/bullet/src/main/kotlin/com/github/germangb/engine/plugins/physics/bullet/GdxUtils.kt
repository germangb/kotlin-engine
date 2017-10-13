package com.github.germangb.engine.plugins.physics.bullet

import com.github.germangb.engine.math.*

internal typealias GdxVector3 = com.badlogic.gdx.math.Vector3
internal typealias GdxMatrix4 = com.badlogic.gdx.math.Matrix4

/** use Gdx's Vector3 class with my own */
internal fun GdxVector3.set(from: Vector3c) = set(from[0], from[1], from[2])

/** Set joml Matrix4 from gdx Matrix4 */
internal fun Matrix4.set(from: GdxMatrix4): Matrix4 {
    m00(from.`val`[GdxMatrix4.M00])
    m10(from.`val`[GdxMatrix4.M01])
    m20(from.`val`[GdxMatrix4.M02])
    m30(from.`val`[GdxMatrix4.M03])

    m01(from.`val`[GdxMatrix4.M10])
    m11(from.`val`[GdxMatrix4.M11])
    m21(from.`val`[GdxMatrix4.M12])
    m31(from.`val`[GdxMatrix4.M13])

    m02(from.`val`[GdxMatrix4.M20])
    m12(from.`val`[GdxMatrix4.M21])
    m22(from.`val`[GdxMatrix4.M22])
    m32(from.`val`[GdxMatrix4.M23])

    m03(from.`val`[GdxMatrix4.M30])
    m13(from.`val`[GdxMatrix4.M31])
    m23(from.`val`[GdxMatrix4.M32])
    m33(from.`val`[GdxMatrix4.M33])
    return this
}

/** Set gdx Vector3 from joml Vector3 */
internal fun GdxMatrix4.set(from: Matrix4c): GdxMatrix4 {
    `val`[GdxMatrix4.M00] = from.m00()
    `val`[GdxMatrix4.M01] = from.m10()
    `val`[GdxMatrix4.M02] = from.m20()
    `val`[GdxMatrix4.M03] = from.m30()

    `val`[GdxMatrix4.M10] = from.m01()
    `val`[GdxMatrix4.M11] = from.m11()
    `val`[GdxMatrix4.M12] = from.m21()
    `val`[GdxMatrix4.M13] = from.m31()

    `val`[GdxMatrix4.M20] = from.m02()
    `val`[GdxMatrix4.M21] = from.m12()
    `val`[GdxMatrix4.M22] = from.m22()
    `val`[GdxMatrix4.M23] = from.m32()

    `val`[GdxMatrix4.M30] = from.m03()
    `val`[GdxMatrix4.M31] = from.m13()
    `val`[GdxMatrix4.M32] = from.m23()
    `val`[GdxMatrix4.M33] = from.m33()
    return this
}
