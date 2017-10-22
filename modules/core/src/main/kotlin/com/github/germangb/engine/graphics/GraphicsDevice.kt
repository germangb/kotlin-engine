package com.github.germangb.engine.graphics

import java.nio.ByteBuffer

/**
 * Low level graphics API
 */
interface GraphicsDevice {
    /**
     * Framebuffer width
     */
    val width: Int

    /**
     * Framebuffer height
     */
    val height: Int

    /**
     * Graphics state
     */
    val state: GraphicsState

    /**
     * Create a texture
     */
    fun createTexture(data: ByteBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter): Texture

    /**
     * Create a framebuffer
     */
    fun createFramebuffer(width: Int, height: Int, targets: List<TexelFormat>, min: TextureFilter, mag: TextureFilter): Framebuffer

    /**
     * Create a mesh
     */
    fun createMesh(vertexData: ByteBuffer, indexData: ByteBuffer, primitive: MeshPrimitive, usage: MeshUsage, vararg attributes: VertexAttribute): Mesh

    /**
     * Create a shader program
     */
    fun createShaderProgram(vertexSource: String, fragmentSource: String): ShaderProgram

    /**
     * Perform a instancing call
     */
    fun instancing(mesh: Mesh, program: ShaderProgram, framebuffer: Framebuffer, action: Instancer.() -> Unit)

    /**
     * Perform a instancing call in the default framebuffer
     */
    fun instancing(mesh: Mesh, program: ShaderProgram, action: Instancer.() -> Unit)
}