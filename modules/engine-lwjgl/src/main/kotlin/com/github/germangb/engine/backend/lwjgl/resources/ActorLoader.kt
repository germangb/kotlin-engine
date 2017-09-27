package com.github.germangb.engine.backend.lwjgl.resources

import com.github.germangb.engine.backend.lwjgl.graphics.GLGraphicsDevice
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.components.addJoint
import com.github.germangb.engine.framework.components.addSkinnedMeshInstance
import com.github.germangb.engine.graphics.Mesh
import com.github.germangb.engine.graphics.Texture
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.resources.AssetManager
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*

/**
 * Load actor blueprint using Assimp
 */
fun loadActorAssimp(manager: AssetManager, loader: LWJGLAssetLoader, graphics: GLGraphicsDevice, path: String): (Actor.() -> Unit)? {
    System.err.println("+ Loading actor blueprint ($path) using Assimp...")

    val flags = aiProcess_Triangulate or aiProcess_FlipUVs or aiProcess_GenSmoothNormals or aiProcess_LimitBoneWeights
    val scene = aiImportFile(path, flags)
    if (scene == null) {
        System.err.println("- ${aiGetErrorString()} ($path)")
        return null
    }

    // print some info
    System.err.println("\t- #meshes = ${scene.mNumMeshes()}")
    System.err.println("\t- #materials = ${scene.mNumMaterials()}")
    System.err.println("\t- #textures = ${scene.mNumTextures()}")
    System.err.println("\t- #animations = ${scene.mNumAnimations()}")

    // read bones
    val bones = mutableMapOf<String, Matrix4c>()
    val boneIds = mutableMapOf<String, Int>()

    var count = 0
    //val set = mutableSetOf<String>()
    (0 until scene.mNumMeshes())
            .map { AIMesh.create(scene.mMeshes()[it]) }
            .forEach { mesh ->
                (0 until mesh.mNumBones())
                        .map { it -> AIBone.create(mesh.mBones()[it]) }
                        .forEach { bone ->
                            val name = bone.mName().dataString()
                            val node = findNode(scene.mRootNode(), name)
                            if (node != null) {
                                bones[name] = Matrix4().set(bone.mOffsetMatrix())
                                boneIds[name] = count
                                count++
                            }
                        }
            }

    // generate "fake" GCed nodes
    System.err.println("\t- Reading assimp hierarchy...")
    val root = FakeAINode(scene.mRootNode())

    // load meshes
    System.err.println("\t- Loading assimp meshes...")
    var boneOff = 0
    val meshes = Array(scene.mNumMeshes(), {
        val aimesh = AIMesh.create(scene.mMeshes()[it])
        val glmesh = loadMesh(graphics, aimesh, skinned = true, boneIds = boneIds)
        manager.delegateMesh("$path/mesh$it", glmesh)
        boneOff += aimesh.mNumBones()
        glmesh
    })

    System.err.println("\t- Loading assimp textures...")

    val file = arrayOf(
            "hellknight.png",
            "gob2_h.png",
            "gob_h.png",
            "tongue.png"
    )

    val textures = Array(scene.mNumMeshes(), {
        loader.loadTexture(file[it])
    })

    System.err.println("\t- Creating blueprint...")
    val actor = root.convert(manager, boneIds, bones, meshes, textures)

    aiFreeScene(scene)
    System.err.println("+ Actor hierarchy loaded ($path)")
    return actor
}

/**
 * Replicate Assimp node so I have something in scope when the actor is built
 */
private class FakeAINode(node: AINode) {
    /** node name */
    val nodeName = node.mName().dataString()

    /** parent node */
    var parent: FakeAINode? = null

    /** LOCAL transform */
    val transform = Matrix4().set(node.mTransformation())

    /** Child nodes */
    val children = mutableListOf<FakeAINode>()

    /** Atached meshes */
    val meshes = mutableListOf<Int>()

    init {
        val meshId = node.mMeshes()
        if (meshId != null) {
            (0 until meshId.capacity()).forEach {
                meshes.add(meshId[it])
            }
        }

        (0 until node.mNumChildren())
                .map { AINode.create(node.mChildren()[it]) }
                .forEach {
                    val child = FakeAINode(it)
                    children.add(child)
                    child.parent = this
                }
    }

    fun convert(manager: AssetManager, boneIds: Map<String, Int>, bones: Map<String, Matrix4c>, aiMeshe: Array<Mesh>, aiTexture: Array<Texture?>): Actor.() -> Unit = {
        val armature = this
        name = nodeName

        // compute transform
        transform.local.set(this@FakeAINode.transform)

        val offset = bones[nodeName]
        val boneId = boneIds[nodeName]
        if (offset != null && boneId != null) {
            //val mat = Matrix4().set(offset)
            //println(mat)
            //println(Matrix4(mat).invert())
            addJoint(boneId, offset)
        }

        this@FakeAINode.children.forEach {
            addChild(it.convert(manager, boneIds, bones, aiMeshe, aiTexture))
        }

        if (meshes.size > 0) {
            meshes.forEach {
                addChild {
                    val armaRoot = armature
                    //addSkinnedMeshInstancer(armaRoot.parent!!, aiMeshe[it], aiTexture[it])
                    addChild {
                        addSkinnedMeshInstance()
                    }
                }
            }
        }

    }
}
/**
 * AI matrix to joml
 */
private fun Matrix4.set(ai: AIMatrix4x4): Matrix4 {
    m00(ai.a1()); m01(ai.b1()); m02(ai.c1()); m03(ai.d1())
    m10(ai.a2()); m11(ai.b2()); m12(ai.c2()); m13(ai.d2())
    m20(ai.a3()); m21(ai.b3()); m22(ai.c3()); m23(ai.d3())
    m30(ai.a4()); m31(ai.b4()); m32(ai.c4()); m33(ai.d4())
    return this
}

/**
 * Find node
 */
private fun findNode(node: AINode, ref: String): AINode? {
    if(node.mName().dataString() == ref) {
        return node
    }
    (0 until node.mNumChildren())
            .map { AINode.create(node.mChildren()[it]) }
            .forEach {
                val test = findNode(it, ref)
                if (test != null) return test
            }
    return null
}

