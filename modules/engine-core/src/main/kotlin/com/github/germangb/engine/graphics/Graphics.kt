package com.github.germangb.engine.graphics

import java.nio.ByteBuffer

/**
 * Low level graphics API
 */
interface Graphics {
    /**
     * Create a texture
     */
    fun createTexture(data: ByteBuffer?, width: Int, height: Int, format: TexelFormat): Texture

    /**
     * Create a framebuffer
     */
    fun createFramebuffer(width: Int, height: Int, targets: List<TexelFormat>): Framebuffer

    /**
     * Create a mesh
     */
    fun createMesh(vertexData: ByteBuffer, indexData: ByteBuffer, primitive: MeshPrimitive, attributes: List<VertexAttribute>): Mesh

    /**
     * Create a shader program
     */
    fun createShaderProgram(vertexSource: String, fragmentSource: String, attributes: List<VertexAttribute>): ShaderProgram

    /**
     * Default framebuffer state
     */
    fun state(action: FramebufferState.() -> Unit)

    /**
     * Perform a render call
     */
    fun render(mesh: Mesh, program: ShaderProgram, framebuffer: Framebuffer, action: Instancer.() -> Unit)

    /**
     * Perform a render call in the default framebuffer
     */
    fun render(mesh: Mesh, program: ShaderProgram, action: Instancer.() -> Unit)
}