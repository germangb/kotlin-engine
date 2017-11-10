package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.backend.dektop.core.glCheckError
import com.github.germangb.engine.backend.dektop.files.DesktopFiles
import com.github.germangb.engine.graphics.*
import com.github.germangb.engine.graphics.InstanceAttribute.TRANSFORM
import com.github.germangb.engine.graphics.TexelFormat.*
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
class GLGraphicsDevice(val files: DesktopFiles, val width: Int, val height: Int) : GraphicsDevice, Destroyable {
    /**
     * Instancing draw call builder
     */
    private val instancer = GLRenderer()
    private val windowFramebuffer = GLFramebuffer(this, 0, width, height, emptyList())

    override fun destroy() {
        instancer.destroy()
    }

    /** GL state thing */
    override val state = GLGraphicsState()

    override val dimensions = FramebufferDimensions(width, height)

    val itextures = mutableListOf<Texture>()
    val imeshes = mutableListOf<Mesh>()
    val ishaders = mutableListOf<ShaderProgram>()
    val ifbos = mutableListOf<Framebuffer>()
    override val textures: List<Texture> get() = itextures
    override val meshes: List<Mesh> get() = imeshes
    override val shaderPrograms: List<ShaderProgram> get() = ishaders
    override val framebuffers: List<Framebuffer> get() = ifbos

    override fun invoke(fbo: Framebuffer, action: GraphicsDevice.() -> Unit) {
        if (fbo is GLFramebuffer) {
            glBindFramebuffer(GL_FRAMEBUFFER, fbo.id)
            if (fbo.drawBuffers.isNotEmpty())
                glDrawBuffers(fbo.drawBuffers)
            action.invoke(this)
            glBindFramebuffer(GL_FRAMEBUFFER, 0)
        }
    }

    override fun invoke(action: GraphicsDevice.() -> Unit) = invoke(windowFramebuffer, action)

    private fun createTexture(data: Buffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean): Texture {
        var id: Int = -1

        glCheckError("Error in createTexture()") {
            id = glGenTextures()

            glBindTexture(GL_TEXTURE_2D, id)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag.glEnum(genMips))
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min.glEnum(genMips))

            if (format == DEPTH24_STENCIL8) {
                // depth stencil
                when (data) {
                    is ByteBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_INT_24_8, data)
                    is FloatBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_INT_24_8, data)
                    is ShortBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_INT_24_8, data)
                    is IntBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_INT_24_8, data)
                }
            } else {
                when (data) {
                    is ByteBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_BYTE, data)
                    is FloatBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_FLOAT, data)
                    is ShortBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_SHORT, data)
                    is IntBuffer? -> glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_INT, data)
                }
            }

            if (genMips) {
                glGenerateMipmap(GL_TEXTURE_2D)
            }

            glBindTexture(GL_TEXTURE_2D, 0)
        }
        return GLTexture(this, id, width, height)
    }

    /** Create OpenGL texture using GL_UNSIGNED_BYTE */
    override fun createTexture(width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter) = createTexture(null as ByteBuffer?, width, height, format, min, mag, false)

    /** Create OpenGL texture using GL_UNSIGNED_BYTE */
    override fun createTexture(data: ByteBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean) = createTexture(data as Buffer?, width, height, format, min, mag, genMips)

    /** Create OpenGL texture using GL_UNSIGNED_SHORT */
    override fun createTexture(data: ShortBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean) = createTexture(data as Buffer?, width, height, format, min, mag, genMips)

    /** Create OpenGL texture using GL_FLOAT */
    override fun createTexture(data: FloatBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean) = createTexture(data as Buffer?, width, height, format, min, mag, genMips)

    /** Create OpenGL texture using GL_UNSIGNED_INT */
    override fun createTexture(data: IntBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter, genMips: Boolean) = createTexture(data as Buffer?, width, height, format, min, mag, genMips)

    /** Check if format is depth */
    fun TexelFormat.isDepth() = (this == DEPTH16 || this == DEPTH24 || this == DEPTH24_STENCIL8)

    /** Check if format stores stencil */
    fun TexelFormat.isStencil() = this == DEPTH24_STENCIL8

    /**
     * Create OpenGL framebuffer
     */
    //texDef: GraphicsDevice.(def: FramebufferTextureDef) -> Texture
    override fun createFramebuffer(width: Int, height: Int, targets: Array<out TexelFormat>, texDef: GraphicsDevice.(def: FramebufferTextureDef) -> Texture): Framebuffer {
        // create textures
        var fbo = -1
        val textures = targets.map { texDef(FramebufferTextureDef(0, width, height, it)) }

        glCheckError("Error in createFramebuffer()") {
            fbo = glGenFramebuffers()
            glBindFramebuffer(GL_FRAMEBUFFER, fbo)

            var colorIndex = GL_COLOR_ATTACHMENT0
            textures.mapIndexed { index, texture -> (targets[index] to texture as GLTexture) }
                    .forEach { (format, texture) ->
                        if (format.isDepth() && format.isStencil()) glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, texture.id, 0)
                        else if (format.isDepth()) glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, texture.id, 0)
                        else if (format.isStencil()) TODO("Handle stencil-only format")
                        else {
                            glFramebufferTexture2D(GL_FRAMEBUFFER, colorIndex, GL_TEXTURE_2D, texture.id, 0)
                            colorIndex++
                        }
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

        return GLMesh(this, vbo, ibo, vao, indexType, indexData.remaining(), primitive, attributes, instanceAttributes ?: emptyArray())
    }

    /**
     * Create a shader program
     */
    override fun createShaderProgram(source: CharSequence): ShaderProgram {
        val vertex = buildString {
            appendln("#define VERTEX_SHADER")
            append(source)
        }

        val fragment = buildString {
            appendln("#define FRAGMENT_SHADER")
            append(source)
        }

        return createShaderProgram(vertex, fragment)
    }

    /**
     * Create a shader program
     */
    override fun createShaderProgram(vertexSource: CharSequence, fragmentSource: CharSequence): ShaderProgram {
        var vertexShader = -1
        var fragmentShader = -1
        var program = -1

        val vertex = buildString {
            appendln("#version 450 core")
            append(vertexSource)
        }

        val fragment = buildString {
            appendln("#version 450 core")
            append(fragmentSource)
        }

        glCheckError("Error in createShaderProgram() while creating vertex skinShader") {
            vertexShader = glCreateShader(GL_VERTEX_SHADER)
            val prepVertex = vertex.inlineIncludes(files)
            //println(prepVertex)
            glShaderSource(vertexShader, prepVertex)
            //println(fragmentSource.inlineIncludes())
            glCompileShader(vertexShader)
            val log = glGetShaderInfoLog(vertexShader)
            if (log.isNotEmpty()) {
                System.err.println("Vertex skinShader compilation error\n$log")
            }
        }

        glCheckError("Error in createShaderProgram() while creating fragment skinShader") {
            fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
            val prepFragment = fragment.inlineIncludes(files)
            glShaderSource(fragmentShader, prepFragment)
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

        return GLShaderProgram(this, program, vertexSource.toString(), fragmentSource.toString())
    }

    /**
     * Render a mesh using instance rendering
     */
    override fun render(mesh: Mesh, program: ShaderProgram, uniforms: Map<String, Any>, instanceData: ByteBuffer) {
        if (mesh is GLMesh && program is GLShaderProgram) instancer.begin(mesh, program, uniforms, instanceData)
    }

    /**
     * Render a mesh
     */
    override fun render(mesh: Mesh, program: ShaderProgram, uniforms: Map<String, Any>) {
        if (mesh is GLMesh && program is GLShaderProgram) instancer.begin(mesh, program, uniforms, null)
    }
}