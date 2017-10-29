package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.backend.dektop.core.glCheckError
import com.github.germangb.engine.graphics.*
import com.github.germangb.engine.graphics.InstanceAttribute.*
import com.github.germangb.engine.utils.Destroyable
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL33.glVertexAttribDivisor
import java.nio.*

/**
 * Lwjgl OpenGL graphics implementation
 */
class GLGraphicsDevice(override val width: Int, override val height: Int) : GraphicsDevice, Destroyable {
    /**
     * Instancing draw call builder
     */
    private val instancer = GLRenderer()
    private val windowFramebuffer = GLFramebuffer(this, 0, width, height, emptyList())

    override fun destroy() {
        instancer.destroy()
    }

    /**
     * Graphics state thing
     */
    override val state = GLGraphicsState()

    val itextures = mutableListOf<Texture>()
    val imeshes = mutableListOf<Mesh>()
    val ishaders = mutableListOf<ShaderProgram>()
    val ifbos = mutableListOf<Framebuffer>()

    override val textures: List<Texture> get() = itextures
    override val meshes: List<Mesh> get() = imeshes
    override val shaderPrograms: List<ShaderProgram> get() = ishaders
    override val framebuffers: List<Framebuffer> get() = ifbos

    private fun createTexture(data: Buffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter): Texture {
        var id: Int = -1

        glCheckError("Error in createTexture()") {
            id = glGenTextures()

            glBindTexture(GL_TEXTURE_2D, id)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag.glEnum)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min.glEnum)
            when (data) {
                is ByteBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_BYTE, data)
                is FloatBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_FLOAT, data)
                is ShortBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_SHORT, data)
                is IntBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_INT, data)
            }
            glBindTexture(GL_TEXTURE_2D, 0)
        }
        return GLTexture(this, id, width, height)
    }

    /** Create OpenGL texture using GL_UNSIGNED_BYTE */
    override fun createTexture(data: ByteBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter) = createTexture(data as Buffer?, width, height, format, min, mag)

    /** Create OpenGL texture using GL_UNSIGNED_SHORT */
    override fun createTexture(data: ShortBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter) = createTexture(data as Buffer?, width, height, format, min, mag)

    /** Create OpenGL texture using GL_FLOAT */
    override fun createTexture(data: FloatBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter) = createTexture(data as Buffer?, width, height, format, min, mag)

    /** Create OpenGL texture using GL_UNSIGNED_INT */
    override fun createTexture(data: IntBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter) = createTexture(data as Buffer?, width, height, format, min, mag)

    /**
     * Check if format is depth
     */
    fun TexelFormat.isDepth() = (this == TexelFormat.DEPTH16 || this == TexelFormat.DEPTH24)

    /**
     * Create OpenGL framebuffer
     */
    override fun createFramebuffer(width: Int, height: Int, targets: List<TexelFormat>, min: TextureFilter, mag: TextureFilter): Framebuffer {
        // create textures
        var fbo = -1
        val textures = targets.map { createTexture(null as ByteBuffer?, width, height, it, min, mag) }

        glCheckError("Error in createFramebuffer()") {
            fbo = glGenFramebuffers()
            glBindFramebuffer(GL_FRAMEBUFFER, fbo)

            // color targets
            textures.map { it as GLTexture }
                    .filterIndexed { i, _ -> !targets[i].isDepth() }
                    .forEachIndexed { index, texture ->
                        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + index, GL_TEXTURE_2D, texture.id, 0)
                    }

            // depth target
            textures.map { it as GLTexture }
                    .filterIndexed { index, _ -> targets[index].isDepth() }
                    .forEach {
                        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, it.id, 0)
                    }

            // check error
            val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
            if (status != GL_FRAMEBUFFER_COMPLETE) {
                System.err.println("FBO error $status")
            }

            glBindFramebuffer(GL_FRAMEBUFFER, 0)

        }
        return GLFramebuffer(this, fbo, width, height, textures)
    }

    /** Create mesh using GL_UNSIGNED_BYTE indices */
    override fun createMesh(vertexData: ByteBuffer, indexData: ByteBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>) = createMesh(vertexData, indexData as Buffer, primitive, usage, attributes, instanceAttributes)

    /** Create mesh using GL_UNSIGNED_SHORT indices */
    override fun createMesh(vertexData: ByteBuffer, indexData: ShortBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>) = createMesh(vertexData, indexData as Buffer, primitive, usage, attributes, instanceAttributes)

    /** Create mesh using GL_UNSIGNED_INT indices */
    override fun createMesh(vertexData: ByteBuffer, indexData: IntBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>) = createMesh(vertexData, indexData as Buffer, primitive, usage, attributes, instanceAttributes)

    /** Create mesh using GL_UNSIGNED_BYTE indices */
    override fun createMesh(vertexData: ByteBuffer, indexData: ByteBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>) = createMesh(vertexData, indexData as Buffer, primitive, usage, attributes, null)

    /** Create mesh using GL_UNSIGNED_SHORT indices */
    override fun createMesh(vertexData: ByteBuffer, indexData: ShortBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>) = createMesh(vertexData, indexData as Buffer, primitive, usage, attributes, null)

    /** Create mesh using GL_UNSIGNED_INT indices */
    override fun createMesh(vertexData: ByteBuffer, indexData: IntBuffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>) = createMesh(vertexData, indexData as Buffer, primitive, usage, attributes, null)

    /**
     * Create a mesh
     */
    private fun createMesh(vertexData: ByteBuffer, indexData: Buffer, primitive: MeshPrimitive, usage: MeshUsage, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>?): Mesh {
        var vbo = -1
        var ibo = -1
        var vao = -1

        // create vbo first
        glCheckError("Error in createMesh while creating vbo") {
            vbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, vertexData, usage.glEnum)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
        }

        // create ibo first
        glCheckError("Error in createMesh while creating vbo") {
            ibo = glGenBuffers()
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
            when (indexData) {
                is ByteBuffer -> glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, usage.glEnum)
                is IntBuffer -> glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, usage.glEnum)
                is ShortBuffer -> glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, usage.glEnum)
            }
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        }

        // create vao
        glCheckError {
            vao = glGenVertexArrays()
            glBindVertexArray(vao)
            glBindBuffer(GL_ARRAY_BUFFER, vbo)

            var offset = 0L
            var stride = 0
            attributes.forEach { stride += it.size * it.type.bytes }

            // per-vertex attributes
            attributes.sorted().forEachIndexed { index, attribute ->
                glEnableVertexAttribArray(index)
                glVertexAttribPointer(index, attribute.size, attribute.type.glEnum, false, stride, offset)
                offset += attribute.size * attribute.type.bytes
            }

            if (instanceAttributes != null) {
                // compute instance buffer stride
                val instanceStride = let {
                    var count = 0
                    instanceAttributes.forEach { count += it.size * it.type.bytes }
                    count
                }

                // per-instance attributes
                glBindBuffer(GL_ARRAY_BUFFER, instancer.buffer)

                var instanceOffset = 0L
                var instanceAttribute = attributes.size
                instanceAttributes.forEach {
                    when (it) {
                        TRANSFORM -> {
                            // matrices are a special case...
                            (0 until 4).forEach {
                                val attrId = instanceAttribute + it
                                glEnableVertexAttribArray(attrId)
                                glVertexAttribPointer(attrId, 4, GL_FLOAT, false, instanceStride, instanceOffset)
                                glVertexAttribDivisor(attrId, 1)
                                instanceOffset += 4 * 4L
                            }
                            instanceAttribute += 4
                        }
                        else -> {
                            glEnableVertexAttribArray(instanceAttribute)
                            glVertexAttribPointer(instanceAttribute, it.size, GL_FLOAT, false, instanceStride, instanceOffset)
                            instanceAttribute++
                            instanceOffset += it.size * it.type.bytes
                        }
                    }
                }
            }

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
            glBindVertexArray(0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
        }

        val indexType = when (indexData) {
            is ByteBuffer -> GL_UNSIGNED_BYTE
            is ShortBuffer -> GL_UNSIGNED_SHORT
            is IntBuffer -> GL_UNSIGNED_INT
            else -> throw IllegalArgumentException("Invalid index type")
        }

        return GLMesh(this, vbo, ibo, vao, indexType, indexData.capacity(), primitive, attributes, instanceAttributes ?: emptyArray())
    }

    /**
     * Create a skinShader program
     */
    override fun createShaderProgram(vertexSource: String, fragmentSource: String): ShaderProgram {
        var vertexShader = -1
        var fragmentShader = -1
        var program = -1

        glCheckError("Error in createShaderProgram() while creating vertex skinShader") {
            vertexShader = glCreateShader(GL_VERTEX_SHADER)
            glShaderSource(vertexShader, vertexSource)
            glCompileShader(vertexShader)
            val log = glGetShaderInfoLog(vertexShader)
            if (log.isNotEmpty()) {
                System.err.println("Vertex skinShader compilation error\n$log")
            }
        }

        glCheckError("Error in createShaderProgram() while creating fragment skinShader") {
            fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
            glShaderSource(fragmentShader, fragmentSource)
            glCompileShader(fragmentShader)
            val log = glGetShaderInfoLog(fragmentShader)
            if (log.isNotEmpty()) {
                System.err.println("Fragment skinShader compilation error\n$log")
            }
        }

        glCheckError("Error in createShaderProgram() while linking skinShader program") {
            program = glCreateProgram()
            glAttachShader(program, vertexShader)
            glAttachShader(program, fragmentShader)
            glLinkProgram(program)
            glDetachShader(program, vertexShader)
            glDetachShader(program, fragmentShader)
            glDeleteShader(vertexShader)
            glDeleteShader(fragmentShader)
        }

        return GLShaderProgram(this, program)
    }

    /**
     * Render a mesh using instance rendering
     */
    override fun renderInstances(mesh: Mesh, program: ShaderProgram, uniforms: Map<String, Any>, framebuffer: Framebuffer, instanceData: ByteBuffer) {
        if (mesh is GLMesh && program is GLShaderProgram && framebuffer is GLFramebuffer) {
            instancer.begin(mesh, program, uniforms, framebuffer, instanceData)
        }
    }

    /**
     * Render a mesh
     */
    override fun render(mesh: Mesh, program: ShaderProgram, uniforms: Map<String, Any>, framebuffer: Framebuffer) {
        if (mesh is GLMesh && program is GLShaderProgram && framebuffer is GLFramebuffer) {
            instancer.begin(mesh, program, uniforms, framebuffer, null)
        }
    }

    /**
     * Render to default framebuffer
     */
    override fun renderInstances(mesh: Mesh, program: ShaderProgram, uniforms: Map<String, Any>, instanceData: ByteBuffer) = renderInstances(mesh, program, uniforms, windowFramebuffer, instanceData)

    /**
     * Render to default framebuffer
     */
    override fun render(mesh: Mesh, program: ShaderProgram, uniforms: Map<String, Any>) = render(mesh, program, uniforms, windowFramebuffer)
}