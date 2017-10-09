package com.github.germangb.engine.backend.lwjgl.assets

import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.assets.MeshAsset
import com.github.germangb.engine.assets.TextureAsset
import com.github.germangb.engine.core.Backend
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.components.*
import com.github.germangb.engine.graphics.*
import com.github.germangb.engine.graphics.TexelFormat.RGBA8
import com.github.germangb.engine.graphics.TextureFilter.LINEAR
import com.github.germangb.engine.graphics.VertexAttribute.*
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import org.lwjgl.assimp.AIBone
import org.lwjgl.assimp.AIMatrix4x4
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.AINode
import org.lwjgl.assimp.Assimp.*
import org.lwjgl.system.jemalloc.JEmalloc.je_free
import org.lwjgl.system.jemalloc.JEmalloc.je_malloc

/**
 * Load actor
 */
fun loadActor(path: String, manager: AssetManager, backend: Backend): (Actor.() -> Unit)? {
    val flags = aiProcess_Triangulate or aiProcess_GenUVCoords or aiProcess_GenNormals or aiProcess_LimitBoneWeights or aiProcess_FlipUVs
    val scene = aiImportFile(path, flags) ?: return null

    // get bones
    val bones = mutableMapOf<String, Pair<Int, Matrix4c>>()
    (0 until scene.mNumMeshes())
            .map { AIMesh.create(scene.mMeshes()[it]) }
            .forEach { mesh ->
                (0 until mesh.mNumBones())
                        .map { AIBone.create(mesh.mBones()[it]) }
                        .forEach {
                            val id = bones.size
                            bones[it.mName().dataString()] = id to Matrix4().set(it.mOffsetMatrix())
                        }
            }

    println(bones.size)

    // get meshes
    val meshes = List(scene.mNumMeshes()) {
        val aimesh = AIMesh.create(scene.mMeshes()[it])
        val attrs = setOf(POSITION, NORMAL, UV, JOINT_ID, JOINT_WEIGHTS)
        val mesh = aiMeshToGL(aimesh, attrs, backend.graphics, bones)
        val meshPath = "$path/mesh_$it"
        manager.delegateMesh(mesh, meshPath)
        MeshAsset(manager, meshPath, attrs) to aimesh.mNumBones()
    }

    // texture assets
    val textures = List(scene.mNumMeshes()) {
        TextureAsset(manager, "hellknight.png", RGBA8, LINEAR, LINEAR)
    }

    // build hierarchy
    val node = SafeNode(scene.mRootNode())
    val blueprint = node.convert(meshes, textures, bones)
    aiFreeScene(scene)
    return blueprint
}

/**
 * Convert aiMesh to engine mesh
 */
fun aiMeshToGL(mesh: AIMesh, attributes: Set<VertexAttribute>, gfx: GraphicsDevice, boneIds: Map<String, Pair<Int, Matrix4c>> = emptyMap()): Mesh {
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
    val indexData = je_malloc(indexDataSize)

    // index data
    (0 until faces.capacity()).map { faces[it] }.forEach {
        val ind = it.mIndices()
        indexData.putInt(ind[0])
        indexData.putInt(ind[1])
        indexData.putInt(ind[2])
    }

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

    // create mesh
    indexData.flip()
    vertexData.flip()
    val glMesh = gfx.createMesh(vertexData, indexData, MeshPrimitive.TRIANGLES, attributes, MeshUsage.STATIC)

    // free resources
    vertexData.clear()
    indexData.clear()
    je_free(vertexData)
    je_free(indexData)
    return glMesh
}

/**
 * Convert aiNode to a kotlin node
 */
class SafeNode(val aiNode: AINode) {
    /** Child nodes */
    val children: List<SafeNode> = List(aiNode.mNumChildren()) {
        val aichild = AINode.create(aiNode.mChildren()[it])
        SafeNode(aichild)
    }

    /** Local transform */
    val transform: Matrix4c = Matrix4().set(aiNode.mTransformation())

    /** Meshes in node */
    val meshes = List(aiNode.mNumMeshes()) {
        aiNode.mMeshes()[it]
    }

    /** node name */
    val name = aiNode.mName().dataString()

    /**
     * Convert hierarchy to blueprint
     */
    fun convert(meshes: List<Pair<MeshAsset, Int>>, textures: List<TextureAsset>, bones: Map<String, Pair<Int, Matrix4c>>): Actor.() -> Unit = {
        // set local transform
        transform.local.set(this@SafeNode.transform)
        name = this@SafeNode.name

        // add joint component
        bones[name]?.let { (id, offset) ->
            addJoint(id, offset)
        }

        // add meshes
        this@SafeNode.meshes.forEach {
            addChild {
                // static mesh
                if (meshes[it].second == 0) {
                    addMeshInstancer(meshes[it].first, textures[it])
                    addChild {
                        addMeshInstance()
                    }
                } else {
                    addSkinnedMeshInstancer(root, meshes[it].first, textures[it])
                    addChild {
                        addSkinnedMeshInstance()
                    }
                }
            }
        }

        // add children
        this@SafeNode.children.forEach {
            addChild(it.convert(meshes, textures, bones))
        }
    }
}

/** AI matrix to joml */
private fun Matrix4.set(ai: AIMatrix4x4): Matrix4 {
    m00(ai.a1()); m01(ai.b1()); m02(ai.c1()); m03(ai.d1())
    m10(ai.a2()); m11(ai.b2()); m12(ai.c2()); m13(ai.d2())
    m20(ai.a3()); m21(ai.b3()); m22(ai.c3()); m23(ai.d3())
    m30(ai.a4()); m31(ai.b4()); m32(ai.c4()); m33(ai.d4())
    return this
}
