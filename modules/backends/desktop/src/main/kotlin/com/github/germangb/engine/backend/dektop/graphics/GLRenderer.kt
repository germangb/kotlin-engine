package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.backend.dektop.core.glCheckError
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.math.*
import com.github.germangb.engine.utils.Destroyable
import org.lwjgl.opengl.GL11.glDrawElements
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL20.glUseProgram
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL31.glDrawElementsInstanced
import org.lwjgl.system.jemalloc.JEmalloc.je_free
import org.lwjgl.system.jemalloc.JEmalloc.je_malloc
import java.nio.ByteBuffer

class GLRenderer : Destroyable {
    companion object {
        val INSTANCE_BUFFER_SIZE = 64 * 4 * 1024L
    }

    /** Buffer size */
    val buffer = glGenBuffers()

    val uniforms = GLUniforms()

    lateinit var shaderProgram: GLShaderProgram
    lateinit var activeMesh: GLMesh

    init {
        glCheckError("Error in GLRenderer.init while creating vertex buffer") {
            val data = je_malloc(INSTANCE_BUFFER_SIZE).asFloatBuffer()
            glBindBuffer(GL_ARRAY_BUFFER, buffer)
            glBufferData(GL_ARRAY_BUFFER, data, GL_STREAM_DRAW)
            glBindBuffer(GL_ARRAY_BUFFER, 0)

            data.clear()
            je_free(data)
        }
    }

    override fun destroy() {
        glCheckError("Error in GLRenderer.free() while deleting vertex buffer") {
            glDeleteBuffers(buffer)
        }
    }

    /**
     * Begin draw call
     */
    fun begin(mesh: GLMesh, program: GLShaderProgram, uniformData: Map<String, Any>, fbo: GLFramebuffer, data: ByteBuffer?) {
        shaderProgram = program
        activeMesh = mesh
        uniforms.setup(program)
        setupInstancing(uniformData, fbo)
        renderInstances(data)
        endInstancing()
    }

    fun setupInstancing(uniformData: Map<String, Any>, fbo: GLFramebuffer) {
        glCheckError("Error in GLRenderer.begin()") {
            glUseProgram(shaderProgram.program)
            glBindVertexArray(activeMesh.vao)
            glBindBuffer(GL_ARRAY_BUFFER, activeMesh.vbo)
            glBindBuffer(GL_ARRAY_BUFFER, buffer)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, activeMesh.ibo)

            uniformData.forEach { unif, value ->
                when (value) {
                    is Float -> uniforms.uniform(unif, value)
                    is Int -> uniforms.uniform(unif, value)
                    is Vector2c -> uniforms.uniform(unif, value)
                    is Vector3c -> uniforms.uniform(unif, value)
                    is Vector4c -> uniforms.uniform(unif, value)
                    is Vector2ic -> uniforms.uniform(unif, value)
                    is Vector3ic -> uniforms.uniform(unif, value)
                    is Vector4ic -> uniforms.uniform(unif, value)
                    is Matrix4c -> uniforms.uniform(unif, value)
                    is Matrix3c -> uniforms.uniform(unif, value)
                    is Texture -> uniforms.uniform(unif, value)
                    is Matrix4Buffer -> uniforms.uniform(unif, value)
                    is Matrix3Buffer -> uniforms.uniform(unif, value)
                }
            }

            // bind framebuffer
            glBindFramebuffer(GL_FRAMEBUFFER, fbo.id)

            if (fbo.drawBuffers.isNotEmpty()) {
                glDrawBuffers(fbo.drawBuffers)
            }
        }
    }

    fun renderInstances(data: ByteBuffer?) {
        glCheckError {
            if (data == null) {
                glDrawElements(activeMesh.primitive.glEnum, activeMesh.indices, activeMesh.indexType, 0L)
            } else {
                glBufferSubData(GL_ARRAY_BUFFER, 0L, data)
                glDrawElementsInstanced(activeMesh.primitive.glEnum, activeMesh.indices, activeMesh.indexType, 0L, data.remaining() / activeMesh.instanceStride)
            }
        }
    }

    fun endInstancing() {
        //renderInstances()
        glCheckError("Error in GLRenderer.end()") {
            glBindFramebuffer(GL_FRAMEBUFFER, 0)
            glUseProgram(0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        }
    }
}