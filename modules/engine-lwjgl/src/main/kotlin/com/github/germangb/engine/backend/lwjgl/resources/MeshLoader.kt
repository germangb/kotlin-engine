package com.github.germangb.engine.backend.lwjgl.resources

import com.github.germangb.engine.backend.lwjgl.graphics.GLGraphicsDevice
import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.MeshPrimitive
import com.github.germangb.engine.graphics.VertexAttribute
import org.lwjgl.assimp.AIBone
import org.lwjgl.assimp.AIMesh
import org.lwjgl.system.jemalloc.JEmalloc

fun loadMesh(graphics: GLGraphicsDevice, aiMesh: AIMesh, skinned: Boolean, boneIds: Map<String, Int> = emptyMap()) : Mesh {
    val primitive = MeshPrimitive.TRIANGLES

    val attributes = if (skinned) {
        listOf(VertexAttribute.POSITION,
                VertexAttribute.NORMAL,
                VertexAttribute.UV,
                VertexAttribute.BONE_ID,
                VertexAttribute.BONE_WEIGHT)
    } else {
        listOf(VertexAttribute.POSITION,
                VertexAttribute.NORMAL,
                VertexAttribute.UV)
    }

    val index = JEmalloc.je_malloc(1000000)
    val data = JEmalloc.je_malloc(1000000)

    // faces
    val faces = aiMesh.mFaces()
    with(index) {
        (0 until aiMesh.mNumFaces()).forEach {
            val indices = faces[it].mIndices()
            putInt(indices[0])
            putInt(indices[1])
            putInt(indices[2])
        }
    }

    // attributes
    with(data) {
        (0 until aiMesh.mNumVertices()).forEach {
            val pos = aiMesh.mVertices()[it]
            val nor = aiMesh.mNormals()[it]
            val tex = aiMesh.mTextureCoords(0)[it]

            putFloat(pos.x())
            putFloat(pos.y())
            putFloat(pos.z())

            putFloat(nor.x())
            putFloat(nor.y())
            putFloat(nor.z())

            putFloat(tex.x())
            putFloat(tex.y())

            if (skinned) {
                putInt(0)
                putInt(0)
                putInt(0)
                putInt(0)

                putFloat(0.0f)
                putFloat(0.0f)
                putFloat(0.0f)
                putFloat(0.0f)
            }
        }
    }

    if (skinned) {
        (0 until aiMesh.mNumBones())
                .forEach {
                    val bone = AIBone.create(aiMesh.mBones()[it])
                    val weights = bone.mNumWeights()
                    val boneId = boneIds[bone.mName().dataString()] ?: -1

                    (0 until weights)
                            .map { bone.mWeights()[it] }
                            .forEach { boneWeight ->
                                val vertex = boneWeight.mVertexId()
                                val weight = boneWeight.mWeight()

                                if (data.getFloat((vertex*16+12)*4) == 0.0f) {
                                    data.putInt((vertex*16+8)*4, boneId)
                                    data.putFloat((vertex*16+12)*4, weight)
                                } else if (data.getFloat((vertex*16+13)*4) == 0.0f) {
                                    data.putInt((vertex*16+9)*4, boneId)
                                    data.putFloat((vertex*16+13)*4, weight)
                                } else if (data.getFloat((vertex*16+14)*4) == 0.0f) {
                                    data.putInt((vertex*16+10)*4, boneId)
                                    data.putFloat((vertex*16+14)*4, weight)
                                } else if (data.getFloat((vertex*16+15)*4) == 0.0f) {
                                    data.putInt((vertex*16+11)*4, boneId)
                                    data.putFloat((vertex*16+15)*4, weight)
                                } else {
                                    throw RuntimeException("More vertex weigths than we have room for!")
                                }
                            }
                }
    }

    data.flip()
    index.flip()

    return graphics.createMesh(data, index, primitive, attributes)
}
