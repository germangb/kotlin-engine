package com.github.germangb.engine.math

import com.github.germangb.engine.core.BufferManager
import java.nio.*

/**
 * Store matrix in matrix buffer
 */
fun Matrix4c.get(buffer: Matrix4Buffer) = get(buffer.container)

/**
 * Store matrix in matrix buffer
 */
fun Matrix3c.get(buffer: Matrix3Buffer) = get(buffer.container)

/**
 * Create matrix buffer from FloatBuffer
 */
fun FloatBuffer.asMatrix4Buffer() = Matrix4Buffer(this)

/**
 * Create matrix buffer from FloatBuffer
 */
fun FloatBuffer.asMatrix3Buffer() = Matrix3Buffer(this)

/**
 * Free math buffer
 */
fun BufferManager.free(buffer: MathBuffer<*, *>) = when (buffer.container) {
    is FloatBuffer -> free(buffer.container)
    is ByteBuffer -> free(buffer.container)
    is IntBuffer -> free(buffer.container)
    is ShortBuffer -> free(buffer.container)
    is LongBuffer -> free(buffer.container)
    else -> {
        throw IllegalArgumentException("Invalid buffer type")
    }
}

/**
 * Custom buffers
 */
sealed class MathBuffer<in T, out BASE : Buffer>(val container: BASE) {
    /**
     * Put thing into buffer
     */
    abstract fun put(value: T)

    /**
     * Put thing into buffer at specific position
     */
    abstract fun put(index: Int, value: T)

    /**
     * Clear buffer
     */
    abstract fun clear()

    /**
     * Flip buffer
     */
    abstract fun flip()

    /**
     * Get position
     */
    abstract fun position(): Int

    /**
     * Set position
     */
    abstract fun position(n: Int)
}

/**
 * Matrix4c buffer
 */
class Matrix4Buffer(container: FloatBuffer) : MathBuffer<Matrix4c, FloatBuffer>(container) {
    private val capacity = container.capacity() / 16
    private var limit = capacity
    private var position = 0

    override fun put(value: Matrix4c) {
        value.get(container)
        val pos = container.position()
        container.position(pos + 16)
    }

    override fun put(index: Int, value: Matrix4c) {
        val head = container.position()
        container.position(16 * index)
        value.get(container)
        container.position(head)
    }

    override fun position() = position

    override fun position(n: Int) {
        container.position(16 * n)
        position = n
    }

    override fun flip() {
        container.flip()
        limit = position
        position = 0
    }

    override fun clear() {
        container.clear()
        limit = capacity
        position = 0
    }
}

/**
 * Matrix3 buffer
 */
class Matrix3Buffer(container: FloatBuffer) : MathBuffer<Matrix3c, FloatBuffer>(container) {
    private val capacity = container.capacity() / 9
    private var limit = capacity
    private var position = 0

    override fun put(value: Matrix3c) {
        value.get(container)
        val pos = container.position()
        container.position(pos + 9)
    }

    override fun put(index: Int, value: Matrix3c) {
        val head = container.position()
        container.position(9 * index)
        value.get(container)
        container.position(head)
    }

    override fun position() = position

    override fun position(n: Int) {
        container.position(16 * n)
        position = n
    }

    override fun flip() {
        container.flip()
        limit = position
        position = 0
    }

    override fun clear() {
        container.clear()
        limit = capacity
        position = 0
    }
}
