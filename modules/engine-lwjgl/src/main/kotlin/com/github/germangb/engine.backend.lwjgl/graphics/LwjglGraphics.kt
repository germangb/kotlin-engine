package com.github.germangb.engine.backend.lwjgl.graphics

import com.github.germangb.engine.backend.lwjgl.glCheckError
import com.github.germangb.engine.graphics.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import java.nio.ByteBuffer

/**
 * Lwjgl OpenGL graphics implementation
 */
class LwjglGraphics : Graphics {
    /**
     * Instancing draw call builder
     */
    val instancer: LwjglInstancer by lazy { LwjglInstancer() }

    /**
     * Create OpenGL texture
     */
    override fun createTexture(data: ByteBuffer?, width: Int, height: Int, format: TexelFormat): Texture {
        var id: Int = -1

        glCheckError("Error in createTexture()") {
            id = glGenTextures()

            glBindTexture(GL_TEXTURE_2D, id)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexImage2D(GL_TEXTURE_2D, 0, format.glEnum, width, height, 0, format.dataFormat, GL_UNSIGNED_BYTE, data)
            glBindTexture(GL_TEXTURE_2D, 0)
        }
        return LwjglTexture(id, width, height)
    }

    /**
     * Check if format is depth
     */
    fun TexelFormat.isDepth() = (this == TexelFormat.DEPTH16 || this == TexelFormat.DEPTH24)

    /**
     * Create OpenGL framebuffer
     */
    override fun createFramebuffer(width: Int, height: Int, targets: List<TexelFormat>): Framebuffer {
        // create textures
        var fbo = -1
        val textures = targets.map { createTexture(null, width, height, it) }

        glCheckError("Error in createFramebuffer()") {
            fbo = glGenFramebuffers()
            glBindFramebuffer(GL_FRAMEBUFFER, fbo)

            // color targets
            textures.map { it as LwjglTexture }
                    .filterIndexed { i, _ -> !targets[i].isDepth() }
                    .forEachIndexed { index, texture ->
                        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+index, GL_TEXTURE_2D, texture.id, 0)
                    }

            // depth target
            textures.map { it as LwjglTexture }
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
        return LwjglFramebuffer(fbo, width, height, textures)
    }

    /**
     * Create a mesh
     */
    override fun createMesh(vertexData: ByteBuffer, indexData: ByteBuffer, primitive: MeshPrimitive, attributes: List<VertexAttribute>): Mesh {
        var vbo = -1
        var ibo = -1
        var vao = -1

        // create vbo first
        glCheckError("Error in createMesh while creating vbo") {
            vbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
        }


        // create ibo first
        glCheckError("Error in createMesh while creating vbo") {
            ibo = glGenBuffers()
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, GL_STATIC_DRAW)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        }

        // create vao
        glCheckError {
            vao = glGenVertexArrays()
            glBindVertexArray(vao)
            glBindBuffer(GL_ARRAY_BUFFER, vbo)

            var offset = 0L
            var stride = 0
            attributes.forEach { stride += it.size }

            attributes.forEachIndexed { index, attribute ->
                glEnableVertexAttribArray(index)
                glVertexAttribPointer(index, attribute.size, GL_FLOAT, false, stride*4, offset)
                offset += attribute.size*4
            }

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
            glBindVertexArray(0)
        }


        return LwjglMesh(vbo, ibo, vao, indexData.capacity()/4, primitive, attributes)
    }

    /**
     * Create a shader program
     */
    override fun createShaderProgram(vertexSource: String, fragmentSource: String, attributes: List<VertexAttribute>): ShaderProgram {
        var vertexShader = -1
        var fragmentShader = -1
        var program = -1

        glCheckError("Error in createShaderProgram() while creating vertex shader") {
            vertexShader = glCreateShader(GL_VERTEX_SHADER)
            glShaderSource(vertexShader, vertexSource)
            glCompileShader(vertexShader)
            val log = glGetShaderInfoLog(vertexShader)
            if (log.isNotEmpty()) {
                System.err.println("Vertex shader compilation error\n$log")
            }
        }

        glCheckError("Error in createShaderProgram() while creating fragment shader") {
            fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
            glShaderSource(fragmentShader, fragmentSource)
            glCompileShader(fragmentShader)
            val log = glGetShaderInfoLog(fragmentShader)
            if (log.isNotEmpty()) {
                System.err.println("Fragment shader compilation error\n$log")
            }
        }

        glCheckError("Error in createShaderProgram() while linking shader program") {
            program = glCreateProgram()
            glAttachShader(program, vertexShader)
            glAttachShader(program, fragmentShader)
            glLinkProgram(program)
        }

        return LwjglShaderProgram(program, vertexShader, fragmentShader, attributes)
    }

    /**
     * GL state
     */
    override fun state(action: FramebufferState.() -> Unit) {
        LwjglFramebufferState().action()
    }

    /**
     * Render a mesh using instance rendering
     */
    override fun render(mesh: Mesh, program: ShaderProgram, framebuffer: Framebuffer, action: Instancer.() -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Render to default framebuffer
     */
    override fun render(mesh: Mesh, program: ShaderProgram, action: Instancer.() -> Unit) {
        instancer.begin(mesh as LwjglMesh, program as LwjglShaderProgram)
        instancer.action()
        instancer.end()
    }

}