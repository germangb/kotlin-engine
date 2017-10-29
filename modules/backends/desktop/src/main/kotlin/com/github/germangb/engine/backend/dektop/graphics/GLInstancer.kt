package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.backend.dektop.core.glCheckError
import com.github.germangb.engine.graphics.Instancer
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.math.*
import com.github.germangb.engine.utils.Destroyable
import org.lwjgl.opengl.GL11.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL20.glUseProgram
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL31.glDrawElementsInstanced
import org.lwjgl.system.jemalloc.JEmalloc.je_free
import org.lwjgl.system.jemalloc.JEmalloc.je_malloc
import java.nio.FloatBuffer

class GLInstancer : Instancer, Destroyable {
    override val transform = Matrix4()

    companion object {
        val MAX_INSTANCES = 1024L
    }

    /** Buffer size */
    private val data = je_malloc(64 * 4 * MAX_INSTANCES).asFloatBuffer()
    private var count = 0
    val buffer = glGenBuffers()

    private val uniformData = je_malloc(10000).asFloatBuffer()
    private val uniforms = GLUniforms(uniformData)

    lateinit var shaderProgram: GLShaderProgram
    lateinit var activeMesh: GLMesh

    init {
        glCheckError("Error in GLInstancer.init while creating vertex buffer") {
            glBindBuffer(GL_ARRAY_BUFFER, buffer)
            glBufferData(GL_ARRAY_BUFFER, data, GL_STREAM_DRAW)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
        }
    }

    override fun destroy() {
        data.clear()
        uniformData.clear()
        je_free(data)
        je_free(uniformData)

        glCheckError("Error in GLInstancer.free() while deleting vertex buffer") {
            glDeleteBuffers(buffer)
        }
    }

    /**
     * Begin draw call
     */
    fun begin(mesh: GLMesh, program: GLShaderProgram, uniformData: Map<String, Any>, fbo: GLFramebuffer) {
        count = 0
        shaderProgram = program
        activeMesh = mesh
        transform.identity()
        uniforms.setup(program)

        glCheckError("Error in GLInstancer.begin()") {
            glUseProgram(program.program)
            glBindVertexArray(mesh.vao)
            glBindBuffer(GL_ARRAY_BUFFER, mesh.vbo)
            glBindBuffer(GL_ARRAY_BUFFER, buffer)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.ibo)

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

    override fun flush() {
        if (count <= 0) return
        data.flip()
        glBufferSubData(GL_ARRAY_BUFFER, 0L, data)
        glDrawElementsInstanced(activeMesh.primitive.glEnum, activeMesh.indices, GL_UNSIGNED_INT, 0L, count)
        data.clear()
        count = 0
    }

    fun end() {
        flush()
        glCheckError("Error in GLInstancer.end()") {
            glBindFramebuffer(GL_FRAMEBUFFER, 0)
            glUseProgram(0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        }
    }

    override fun instance() {
        if (count + 1 > MAX_INSTANCES) flush()
        count++
        transform.get(data).position(16 * count)
    }
}