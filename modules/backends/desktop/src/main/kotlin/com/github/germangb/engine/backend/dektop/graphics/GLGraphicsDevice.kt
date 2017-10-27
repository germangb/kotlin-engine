package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.backend.dektop.core.glCheckError
import com.github.germangb.engine.utils.Destroyable
import com.github.germangb.engine.graphics.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL33.glVertexAttribDivisor
import java.nio.ByteBuffer

/**
 * Lwjgl OpenGL graphics implementation
 */
class GLGraphicsDevice(override val width: Int, override val height: Int) : GraphicsDevice, Destroyable {
    /**
     * Instancing draw call builder
     */
    private val instancer = GLInstancer()
    private val windowFramebuffer = GLFramebuffer(0, width, height, emptyList())

    override fun destroy() {
        instancer.destroy()
    }

    /**
     * Graphics state thing
     */
    override val state = GLGraphicsState()

    /**
     * Create OpenGL texture
     */
    override fun createTexture(data: ByteBuffer?, width: Int, height: Int, format: TexelFormat, min: TextureFilter, mag: TextureFilter): Texture {
        var id: Int = -1

        glCheckError("Error in createTexture()") {
            id = glGenTextures()

            glBindTexture(GL_TEXTURE_2D, id)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag.glEnum)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min.glEnum)
            glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_BYTE, data)
            glBindTexture(GL_TEXTURE_2D, 0)
        }
        return GLTexture(id, width, height)
    }

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
        val textures = targets.map { createTexture(null, width, height, it, min, mag) }

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
        return GLFramebuffer(fbo, width, height, textures)
    }

    /**
     * Create a mesh
     */
    override fun createMesh(vertexData: ByteBuffer, indexData: ByteBuffer, primitive: MeshPrimitive, usage: MeshUsage, vararg attributes: VertexAttribute): Mesh {
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
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, usage.glEnum)
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

            // per-instance attributes
            glBindBuffer(GL_ARRAY_BUFFER, instancer.buffer)
            (0 until 4).forEach {
                glEnableVertexAttribArray(attributes.size + it)
                glVertexAttribPointer(attributes.size + it, 4, GL_FLOAT, false, 16 * 4, (4 * it) * 4L)
                glVertexAttribDivisor(attributes.size + it, 1)
            }

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
            glBindVertexArray(0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
        }


        return GLMesh(vbo, ibo, vao, indexData.capacity() / 4, primitive, attributes)
    }

    /**
     * Create a skinShader program
     */
    override fun <T> createShaderProgram(vertexSource: String, fragmentSource: String): ShaderProgram<T> {
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

        return GLShaderProgram(program)
    }

    /**
     * Render a mesh using instance rendering
     */
    override fun <T> instancing(mesh: Mesh, program: ShaderProgram<T>, uniforms: T, framebuffer: Framebuffer, action: Instancer.() -> Unit) {
        if (mesh is GLMesh && program is GLShaderProgram && framebuffer is GLFramebuffer) {
            instancer.begin(mesh, program, uniforms as Any, framebuffer)
            instancer.action()
            instancer.end()
        }
    }

    /**
     * Render bind default framebuffer
     */
    override fun <T> instancing(mesh: Mesh, program: ShaderProgram<T>, uniforms: T, action: Instancer.() -> Unit) = instancing(mesh, program, uniforms, windowFramebuffer, action)

}