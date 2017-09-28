package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.MeshPrimitive
import com.github.germangb.engine.graphics.MeshUsage
import com.github.germangb.engine.graphics.TestFunction
import com.github.germangb.engine.graphics.TexelFormat
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL30.*

/** Buffer usage */
val MeshUsage.glEnum get() = when(this) {
    MeshUsage.STATIC -> GL_STATIC_DRAW
    MeshUsage.DYNAMIC -> GL_DYNAMIC_DRAW
    MeshUsage.STREAM -> GL_STREAM_DRAW
}

/**
 * Primitive enums
 */
val MeshPrimitive.glEnum get() = when(this) {
    MeshPrimitive.TRIANGLES -> GL_TRIANGLES
    MeshPrimitive.TRIANGLE_STRIP -> GL_TRIANGLE_STRIP
    MeshPrimitive.LINES -> GL_LINES
}

/**
 * Test function to GLEnum
 */
val TestFunction.glEnum get() = when(this) {
    TestFunction.EQUAL -> GL_EQUAL
    TestFunction.NOT_EQUAL -> GL_NOTEQUAL
    TestFunction.LESS -> GL_LESS
    TestFunction.GREATER -> GL_GREATER
    TestFunction.LESS_OR_EQUAL -> GL_LEQUAL
    TestFunction.GREATER_OR_EQUAL -> GL_GEQUAL
    else -> -1
}

/**
 * Convert texel format to GLEnum
 */
val TexelFormat.glEnum get() = when(this) {
    TexelFormat.RGBA8 -> GL_RGBA8
    TexelFormat.RGB8 -> GL_RGB8
    TexelFormat.RG8 -> GL_RG8
    TexelFormat.R8 -> GL_R8
    TexelFormat.RGBA16 -> GL_RGBA16
    TexelFormat.RGB16 -> GL_RGB16
    TexelFormat.RG16 -> GL_RG16
    TexelFormat.R16 -> GL_R16
    TexelFormat.DEPTH16 -> GL_DEPTH_COMPONENT16
    TexelFormat.DEPTH24 -> GL_DEPTH_COMPONENT24
}

/**
 * Convert texel format to GLEnum
 */
val TexelFormat.dataFormat get() = when(this) {
    TexelFormat.RGBA8 -> GL_RGBA
    TexelFormat.RGB8 -> GL_RGB
    TexelFormat.RG8 -> GL_RG
    TexelFormat.R8 -> GL_R
    TexelFormat.RGBA16 -> GL_RGBA
    TexelFormat.RGB16 -> GL_RGB
    TexelFormat.RG16 -> GL_RG
    TexelFormat.R16 -> GL_R
    TexelFormat.DEPTH16 -> GL_DEPTH_COMPONENT
    TexelFormat.DEPTH24 -> GL_DEPTH_COMPONENT
}
