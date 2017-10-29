package com.github.germangb.engine.graphics

import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

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
     * Get active textures
     */
    val textures: List<Texture>

    /**
     * Get active meshes
     */
    val meshes: List<Mesh>

    /**
     * Get active shaderPrograms
     */
    val shaderPrograms: List<ShaderProgram>

    /**
     * Get active framebuffers
     */
    val framebuffers: List<Framebuffer>

    /**
     * Create a texture from 8bit components
     */
    fun createTexture(data: ByteBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter): Texture

    /**
     * Create a texture from 16bit components
     */
    fun createTexture(data: ShortBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter): Texture

    /**
     * Create a texture from float32 components
     */
    fun createTexture(data: FloatBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter): Texture

    /**
     * Create a texture from 32bit components
     */
    fun createTexture(data: IntBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter): Texture

    /**
     * Create a framebuffer
     */
    fun createFramebuffer(width: Int, height: Int, targets: List<TexelFormat>, min: TextureFilter, mag: TextureFilter): Framebuffer

    /**
     * Create a mesh
     */
    fun createMesh(vertexData: ByteBuffer, indexData: ByteBuffer, primitive: MeshPrimitive, usage: MeshUsage, vararg attributes: VertexAttribute): Mesh

    /**
     * Create a mesh
     */
    fun createMesh(vertexData: ByteBuffer, indexData: ShortBuffer, primitive: MeshPrimitive, usage: MeshUsage, vararg attributes: VertexAttribute): Mesh

    /**
     * Create a mesh
     */
    fun createMesh(vertexData: ByteBuffer, indexData: IntBuffer, primitive: MeshPrimitive, usage: MeshUsage, vararg attributes: VertexAttribute): Mesh

    /**
     * Create a shader program
     */
    fun createShaderProgram(vertexSource: String, fragmentSource: String): ShaderProgram

    /**
     * Perform a instancing call
     */
    fun instancing(mesh: Mesh, program: ShaderProgram, uniforms: Map<String, Any>, framebuffer: Framebuffer, action: Instancer.() -> Unit)

    /**
     * Perform a instancing call in the default framebuffer
     */
    fun instancing(mesh: Mesh, program: ShaderProgram, uniforms: Map<String, Any>, action: Instancer.() -> Unit)
}