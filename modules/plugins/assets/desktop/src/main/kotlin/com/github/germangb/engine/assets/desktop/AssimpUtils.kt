package com.github.germangb.engine.assets.desktop

import com.github.germangb.engine.graphics.*
import com.github.germangb.engine.graphics.VertexAttribute.*
import com.github.germangb.engine.math.Matrix4c
import org.lwjgl.assimp.AIBone
import org.lwjgl.assimp.AIMesh
import org.lwjgl.system.jemalloc.JEmalloc.je_free
import org.lwjgl.system.jemalloc.JEmalloc.je_malloc

/**
 * Convert aiMesh bind engine mesh
 */
fun aiMeshToGL(mesh: AIMesh, attributes: Array<out VertexAttribute>, instanceAttributes: Array<out InstanceAttribute>, usage: MeshUsage, gfx: GraphicsDevice, boneIds: Map<String, Pair<Int, Matrix4c>> = emptyMap()): Mesh {
    // mesh attributes
    val positions = mesh.mVertices()
    val normals = mesh.mNormals()
    val uvs = mesh.mTextureCoords(0)

    // faces
    val faces = mesh.mFaces()

    // vertex stride
    val vertexSize by lazy {
        var count = 0
        attributes.forEach { count += it.size }
        count
    }

    // allocate data
    val vertexDataSize = vertexSize * positions.capacity() * 4L
    val indexDataSize = 3 * faces.capacity() * 4L
    val vertexData = je_malloc(vertexDataSize)
    val indexData = je_malloc(indexDataSize).asIntBuffer()

    // index data
    (0 until faces.capacity()).map { faces[it] }.forEach {
        val ind = it.mIndices()
        indexData.put(ind[0])
        indexData.put(ind[1])
        indexData.put(ind[2])
    }

    val hasSkin = JOINT_IDS in attributes

    fun VertexAttribute.addData(i: Int) {
        when (this) {
            POSITION -> {
                vertexData.putFloat(positions[i].x())
                vertexData.putFloat(positions[i].y())
                vertexData.putFloat(positions[i].z())
            }
            NORMAL -> {
                vertexData.putFloat(normals[i].x())
                vertexData.putFloat(normals[i].y())
                vertexData.putFloat(normals[i].z())
            }
            UV -> {
                vertexData.putFloat(uvs[i].x())
                vertexData.putFloat(uvs[i].y())
            }
            else -> {
                (0 until size).forEach { vertexData.putFloat(0f) }
            }
        }
    }

    // vertex data
    (0 until mesh.mNumVertices()).forEach {
        attributes.sorted().forEach { attr -> attr.addData(it) }
    }

    if (hasSkin) {

        // add bone weights
        (0 until mesh.mNumBones())
                .map { AIBone.create(mesh.mBones()[it]) }
                .forEach { bone ->
                    val name = bone.mName().dataString()
                    val boneId = boneIds[name]?.first ?: -1
                    val weights = bone.mWeights()

                    (0 until bone.mNumWeights())
                            .map { weights[it] }
                            .forEach {
                                val vertId = it.mVertexId()
                                val weight = it.mWeight()
                                val offset = vertexSize * 4 * vertId

                                when {
                                    vertexData.getFloat(offset + 12 * 4) == 0.0f -> {
                                        vertexData.putFloat(offset + 12 * 4, weight)
                                        vertexData.putFloat(offset + 8 * 4, boneId.toFloat())
                                    }
                                    vertexData.getFloat(offset + 13 * 4) == 0.0f -> {
                                        vertexData.putFloat(offset + 13 * 4, weight)
                                        vertexData.putFloat(offset + 9 * 4, boneId.toFloat())
                                    }
                                    vertexData.getFloat(offset + 14 * 4) == 0.0f -> {
                                        vertexData.putFloat(offset + 14 * 4, weight)
                                        vertexData.putFloat(offset + 10 * 4, boneId.toFloat())
                                    }
                                    vertexData.getFloat(offset + 15 * 4) == 0.0f -> {
                                        vertexData.putFloat(offset + 15 * 4, weight)
                                        vertexData.putFloat(offset + 11 * 4, boneId.toFloat())
                                    }
                                    else -> throw Exception("Ran out of joint slots!")
                                }
                            }
                }
    }

    // create mesh
    indexData.flip()
    vertexData.flip()
    val glMesh = gfx.createMesh(vertexData, indexData, MeshPrimitive.TRIANGLES, usage, attributes, instanceAttributes)

    // free resources
    vertexData.clear()
    indexData.clear()
    je_free(vertexData)
    je_free(indexData)
    return glMesh
}

