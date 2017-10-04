package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.graphics.*
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

/** Filters */
val TextureFilter.glEnum get() = when(this) {
    TextureFilter.NEAREST -> GL_NEAREST
    TextureFilter.LINEAR -> GL_LINEAR
}

/**
 * Primitive enums
 */
val MeshPrimitive.glEnum get() = when(this) {
    MeshPrimitive.TRIANGLES -> GL_TRIANGLES
    MeshPrimitive.TRIANGLE_STRIP -> GL_TRIANGLE_STRIP
    MeshPrimitive.TRIANGLE_FAN -> GL_TRIANGLE_FAN
    MeshPrimitive.LINES -> GL_LINES
    MeshPrimitive.LINE_LOOP -> GL_LINE_LOOP
    MeshPrimitive.LINE_STRIP -> GL_LINE_STRIP
    MeshPrimitive.POINTS -> GL_POINTS
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
    TexelFormat.RGBA16F -> GL_RGBA16F
    TexelFormat.RGBA32F -> GL_RGBA32F
    TexelFormat.RGB16F -> GL_RGB16F
    TexelFormat.RGB32F -> GL_RGB32F
    TexelFormat.RGBA8 -> GL_RGBA8
    TexelFormat.RGB8 -> GL_RGB8
    TexelFormat.RG16F -> GL_RG16F
    TexelFormat.RG32F -> GL_RG32F
    TexelFormat.RG8 -> GL_RG8
    TexelFormat.R16F -> GL_R16F
    TexelFormat.R32F -> GL_R32F
    TexelFormat.R8 -> GL_R8
    TexelFormat.RGBA16 -> GL_RGBA16
    TexelFormat.RGB16 -> GL_RGB16
    TexelFormat.RG16 -> GL_RG16
    TexelFormat.R16 -> GL_R16
    TexelFormat.DEPTH16 -> GL_DEPTH_COMPONENT16
    TexelFormat.DEPTH24 -> GL_DEPTH_COMPONENT24
    TexelFormat.DEPTH24_STENCIL8 -> GL_DEPTH24_STENCIL8
}

/**
 * Convert texel format to GLEnum
 */
val TexelFormat.dataFormat get() = when(this) {
    TexelFormat.RGBA16F -> GL_RGBA
    TexelFormat.RGBA32F -> GL_RGBA
    TexelFormat.RGBA8 -> GL_RGBA
    TexelFormat.RGB16F -> GL_RGB
    TexelFormat.RGB32F -> GL_RGB
    TexelFormat.RGB8 -> GL_RGB
    TexelFormat.RG16F -> GL_RG
    TexelFormat.RG32F -> GL_RG
    TexelFormat.RG8 -> GL_RG
    TexelFormat.R16F -> GL_RED
    TexelFormat.R32F -> GL_RED
    TexelFormat.R8 -> GL_RED
    TexelFormat.RGBA16 -> GL_RGBA
    TexelFormat.RGB16 -> GL_RGB
    TexelFormat.RG16 -> GL_RG
    TexelFormat.R16 -> GL_RED
    TexelFormat.DEPTH16 -> GL_DEPTH_COMPONENT
    TexelFormat.DEPTH24 -> GL_DEPTH_COMPONENT
    TexelFormat.DEPTH24_STENCIL8 -> GL_DEPTH_STENCIL
}
