package com.github.germangb.engine.graphics

import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

/** Low level graphics API */
interface GraphicsDevice {
    /** Default Framebuffer dimensions */
    val dimensions: FramebufferDimensions

    /** Graphics state */
    val state: GraphicsState

    /** Get active textures */
    val textures: List<Texture>

    /** Get active meshes */
    val meshes: List<Mesh>

    /** Get active shaderPrograms */
    val shaderPrograms: List<ShaderProgram>

    /** Get active framebuffers */
    val framebuffers: List<Framebuffer>

    /** Work with some framebuffer */
    operator fun invoke(fbo: Framebuffer, action: GraphicsDevice.() -> Unit)

    /** Work with default framebuffer  */
    operator fun invoke(action: GraphicsDevice.() -> Unit)

    /** Perform instancing call to the framebuffer */
    fun render(mesh: Mesh, program: ShaderProgram, uniforms: Map<String, Any>, instanceData: ByteBuffer)

    /** Perform draw call to the framebuffer */
    fun render(mesh: Mesh, program: ShaderProgram, uniforms: Map<String, Any>)

    /** Create a texture with garbage data */
    fun createTexture(width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean = false): Texture

    /** Create a texture from 8bit components */
    fun createTexture(data: ByteBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean = false): Texture

    /** Create a texture from 16bit components */
    fun createTexture(data: ShortBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean = false): Texture

    /** Create a texture from float32 components */
    fun createTexture(data: FloatBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean = false): Texture

    /** Create a texture from 32bit components */
    fun createTexture(data: IntBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean = false): Texture

    /** Create a framebuffer */
    fun createFramebuffer(width: Int, height: Int, targets: Array<out TexelFormat>, texDef: GraphicsDevice.(def: FramebufferTextureDef) -> Texture = defaulFramebufferDef): Framebuffer

    /** Create a mesh */
    fun createMesh(vertexData: ByteBuffer, indexData: ByteBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>): Mesh

    /** Create a mesh */
    fun createMesh(vertexData: ByteBuffer, indexData: ShortBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>): Mesh

    /** Create a mesh */
    fun createMesh(vertexData: ByteBuffer, indexData: IntBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>): Mesh

    /** Create a mesh */
    fun createMesh(vertexData: ByteBuffer, indexData: IntBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>): Mesh

    /** Create a mesh */
    fun createMesh(vertexData: ByteBuffer, indexData: ByteBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>): Mesh

    /** Create a mesh */
    fun createMesh(vertexData: ByteBuffer, indexData: ShortBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>): Mesh

    /** Create a shader program */
    fun createShaderProgram(vertexSource: CharSequence, fragmentSource: CharSequence): ShaderProgram

    /** Create a shader program */
    fun createShaderProgram(source: CharSequence): ShaderProgram
}