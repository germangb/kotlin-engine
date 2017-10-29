package com.github.germangb.engine.graphics

enum class TexelFormat(val channels: Int) {
    /**
     * RGBA 8bits per component
     */
    RGBA16F(4),

    /**
     * RGBA 8bits per component
     */
    RGBA32F(4),

    /**
     * RGBA 8bits per component
     */
    RGBA8(4),

    /**
     * RGB 8bits per component
     */
    RGB16F(3),

    /**
     * RGB 8bits per component
     */
    RGB32F(3),

    /**
     * RGB 8bits per component
     */
    RGB8(3),

    /**
     * RG 8bits per component
     */
    RG16F(2),

    /**
     * RG 8bits per component
     */
    RG32F(2),

    /**
     * RG 8bits per component
     */
    RG8(2),

    /**
     * R 8bits per component
     */
    R16F(1),

    /**
     * R 8bits per component
     */
    R32F(1),

    /**
     * R 8bits per component
     */
    R8(1),

    /**
     * RGBA 16bits per component
     */
    RGBA16(4),

    /**
     * RGB 16bits per component
     */
    RGB16(3),

    /**
     * RG 16bits per component
     */
    RG16(2),

    /**
     * R 16bits per component
     */
    R16(1),

    /**
     * Depth component 16bits
     */
    DEPTH16(1),

    /**
     * Depth component 24bits
     */
    DEPTH24(1),

    /**
     * Depth & stencil
     */
    DEPTH24_STENCIL8(2)
}