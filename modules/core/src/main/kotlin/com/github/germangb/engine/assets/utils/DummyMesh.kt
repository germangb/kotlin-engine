package com.github.germangb.engine.assets.utils

import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.MeshPrimitive
import com.github.germangb.engine.graphics.VertexAttribute
import java.nio.ByteBuffer

object DummyMesh : Mesh {
    override val attributes = emptyArray<VertexAttribute>()
    override val primitive = MeshPrimitive.TRIANGLES
    override fun setVertexData(data: ByteBuffer, offset: Long) = Unit
    override fun setIndexData(data: ByteBuffer, offset: Long) = Unit
    override fun destroy() = Unit
    override var indices: Int
        get() = 0
        set(value) = Unit
}
