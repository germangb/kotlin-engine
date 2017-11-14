package com.github.germangb.engine.graphics

import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

/** Low level graphics API */
interface GraphicsDevice {
    /** Default Framebuffer dimensions */
    val dimensions: FramebufferDimensions

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

    /** Clear color buffer */
    fun clearColor(r: Float, g: Float, b: Float, a: Float)

    /** Clear depth buffer */
    fun clearDepth(d: Float)

    /** Clear stencil buffer */
    fun clearStencil(s: Int)

    /** Render mode */
    fun polygonMode(mode: DrawMode)

    /** Set line width */
    fun lineWidth(width: Float)

    /** Set point size */
    fun pointSize(size: Float)

    /** Set depth test function */
    fun depthTest(func: TestFunction)

    /** Stencil test function */
    fun stencilFunc(func: TestFunction, ref: Int, mask: Int)

    /** Stencil operation */
    fun stencilOp(sfail: StencilOperation, dfail: StencilOperation, dpass: StencilOperation)

    /** Set culling mode */
    fun cullMode(mode: CullingMode)

    /** Set viewport */
    fun viewPort(x: Int, y: Int, width: Int, height: Int)

    /** Clear color buffer */
    fun clearColorBuffer()

    /** Clear depth buffer */
    fun clearDepthBuffer()

    /** Clear stencil buffer */
    fun clearStencilBuffer()
}