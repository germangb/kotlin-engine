package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.graphics.*
import com.github.germangb.engine.graphics.MeshPrimitive.*
import com.github.germangb.engine.graphics.MeshUsage.*
import com.github.germangb.engine.graphics.TestFunction.*
import com.github.germangb.engine.graphics.TexelFormat.*
import com.github.germangb.engine.graphics.TextureFilter.LINEAR
import com.github.germangb.engine.graphics.TextureFilter.NEAREST
import com.github.germangb.engine.graphics.VertexAttributeType.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL30.*

/**
 * Render mode
 */
val DrawMode.glEnum
    get() = when (this) {
        DrawMode.WIREFRAME -> GL_LINE
        DrawMode.SOLID -> GL_FILL
    }

/**
 * Stencil op GLEnum
 */
val StencilOperation.glEnum
    get() = when (this) {
        StencilOperation.KEEP -> GL_KEEP
        StencilOperation.NEGATE -> GL_INVERT
        StencilOperation.REPLACE -> GL_REPLACE
        StencilOperation.INCREMENT -> GL_INCR
        StencilOperation.INCREMENT_WRAP -> GL_INCR_WRAP
        StencilOperation.DECREMENT -> GL_DECR
        StencilOperation.DECREMENT_WRAP -> GL_DECR_WRAP
        StencilOperation.ZERO -> GL_ZERO
    }

/**
 * Culling mode
 */
val CullingMode.glEnum
    get() = when (this) {
        CullingMode.DISABLED -> -1
        CullingMode.BACK -> GL_BACK
        CullingMode.FRONT -> GL_FRONT
        CullingMode.FRONT_AND_BACK -> GL_FRONT_AND_BACK
    }

/** Attribute type */
val VertexAttributeType.glEnum
    get() = when (this) {
        FLOAT -> GL_FLOAT
        INT -> GL_INT
        SHORT -> GL_SHORT
        BYTE -> GL_BYTE
    }

/** Buffer usage */
val MeshUsage.glEnum
    get() = when (this) {
        STATIC -> GL_STATIC_DRAW
        DYNAMIC -> GL_DYNAMIC_DRAW
        STREAM -> GL_STREAM_DRAW
    }

/** Filters */
fun TextureFilter.glEnum(mips: Boolean) = when (this) {
    NEAREST -> if (mips) GL_NEAREST_MIPMAP_NEAREST else GL_NEAREST
    LINEAR -> if (mips) GL_LINEAR_MIPMAP_LINEAR else GL_LINEAR
}

/**
 * Primitive enums
 */
val MeshPrimitive.glEnum
    get() = when (this) {
        QUADS -> GL_QUADS
        TRIANGLES -> GL_TRIANGLES
        TRIANGLE_STRIP -> GL_TRIANGLE_STRIP
        TRIANGLE_FAN -> GL_TRIANGLE_FAN
        LINES -> GL_LINES
        LINE_LOOP -> GL_LINE_LOOP
        LINE_STRIP -> GL_LINE_STRIP
        POINTS -> GL_POINTS
    }

/**
 * Test function bind GLEnum
 */
val TestFunction.glEnum
    get() = when (this) {
        EQUAL -> GL_EQUAL
        NOT_EQUAL -> GL_NOTEQUAL
        LESS -> GL_LESS
        GREATER -> GL_GREATER
        LESS_OR_EQUAL -> GL_LEQUAL
        GREATER_OR_EQUAL -> GL_GEQUAL
        ALWAYS -> GL_ALWAYS
        NEVER -> GL_NEVER
        else -> -1
    }

/**
 * Convert texel format bind GLEnum
 */
val TexelFormat.glEnum
    get() = when (this) {
        RGBA16F -> GL_RGBA16F
        RGBA32F -> GL_RGBA32F
        RGB16F -> GL_RGB16F
        RGB32F -> GL_RGB32F
        RGBA8 -> GL_RGBA8
        RGB8 -> GL_RGB8
        RG16F -> GL_RG16F
        RG32F -> GL_RG32F
        RG8 -> GL_RG8
        R16F -> GL_R16F
        R32F -> GL_R32F
        R8 -> GL_R8
        RGBA16 -> GL_RGBA16
        RGB16 -> GL_RGB16
        RG16 -> GL_RG16
        R16 -> GL_R16
        DEPTH16 -> GL_DEPTH_COMPONENT16
        DEPTH24 -> GL_DEPTH_COMPONENT24
        DEPTH24_STENCIL8 -> GL_DEPTH24_STENCIL8
        RGB4 -> GL_RGB4
        R3_G3_B2 -> GL_R3_G3_B2
        RGBA4 -> GL_RGBA4
    }

/**
 * Convert texel format bind GLEnum
 */
val TexelFormat.dataFormat
    get() = when (this) {
        RGBA16F -> GL_RGBA
        RGBA32F -> GL_RGBA
        RGBA8 -> GL_RGBA
        RGB16F -> GL_RGB
        RGB32F -> GL_RGB
        RGB8 -> GL_RGB
        RG16F -> GL_RG
        RG32F -> GL_RG
        RG8 -> GL_RG
        R16F -> GL_RED
        R32F -> GL_RED
        R8 -> GL_RED
        RGBA16 -> GL_RGBA
        RGB16 -> GL_RGB
        RG16 -> GL_RG
        R16 -> GL_RED
        DEPTH16 -> GL_DEPTH_COMPONENT
        DEPTH24 -> GL_DEPTH_COMPONENT
        DEPTH24_STENCIL8 -> GL_DEPTH_STENCIL
        RGB4 -> GL_RGB
        R3_G3_B2 -> GL_RGB
        RGBA4 -> GL_RGBA
    }
