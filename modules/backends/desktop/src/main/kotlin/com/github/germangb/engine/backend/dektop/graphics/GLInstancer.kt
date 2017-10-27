package com.github.germangb.engine.backend.dektop.graphics

import com.github.germangb.engine.backend.dektop.core.glCheckError
import com.github.germangb.engine.graphics.Instancer
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.graphics.Uniform
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.utils.Destroyable
import org.lwjgl.opengl.GL11.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL20.glUseProgram
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL31.glDrawElementsInstanced
import org.lwjgl.system.jemalloc.JEmalloc.je_free
import org.lwjgl.system.jemalloc.JEmalloc.je_malloc
import kotlin.reflect.full.memberProperties

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

    lateinit var shaderProgram: GLShaderProgram<*>
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
    fun <T> begin(mesh: GLMesh, program: GLShaderProgram<T>, uniforms: Any, fbo: GLFramebuffer) {
        count = 0
        shaderProgram = program
        activeMesh = mesh
        transform.identity()

        glCheckError("Error in GLInstancer.begin()") {
            glUseProgram(program.program)
            glBindVertexArray(mesh.vao)
            glBindBuffer(GL_ARRAY_BUFFER, mesh.vbo)
            glBindBuffer(GL_ARRAY_BUFFER, buffer)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.ibo)

            // set shader uniforms
            val uniformSetter = GLUniforms(program, uniformData)
            uniforms::class.memberProperties.mapNotNull { prop ->
                prop.annotations.find { it is Uniform }?.let {
                    Pair(it as Uniform, prop)
                }
            }.forEach { (ann, prop) ->
                val value = prop.getter.call(uniforms)
                //println("${ann.name} = $value")
                when(value) {
                    is Matrix4c -> uniformSetter.uniform(ann.name, value)
                    is Texture -> uniformSetter.uniform(ann.name, value)
                    is Array<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        if (value.isNotEmpty() && value[0] is Matrix4c) uniformSetter.uniform(ann.name, value as Array<Matrix4c>)
                    }
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

//    override fun uniforms(action: Uniforms.() -> Unit) {
//        GLUniforms(shaderProgram, uniformData).action()
//    }
}