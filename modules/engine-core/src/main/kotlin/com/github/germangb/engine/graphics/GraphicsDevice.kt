package com.github.germangb.engine.graphics

import java.nio.ByteBuffer

/**
 * Low level graphics API
 */
interface GraphicsDevice {
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
    fun createMesh(vertexData: ByteBuffer, indexData: ByteBuffer, primitive: MeshPrimitive, attributes: Set<VertexAttribute>, usage: MeshUsage = MeshUsage.STATIC): Mesh

    /**
     * Create a shader program
     */
    fun createShaderProgram(vertexSource: String, fragmentSource: String): ShaderProgram

    /**
     * Default framebuffer state
     */
    fun state(action: GraphicsState.() -> Unit)

    /**
     * Perform a render call
     */
    fun render(mesh: Mesh, program: ShaderProgram, framebuffer: Framebuffer, action: Instancer.() -> Unit)

    /**
     * Perform a render call in the default framebuffer
     */
    fun render(mesh: Mesh, program: ShaderProgram, action: Instancer.() -> Unit)
}