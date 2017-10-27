package com.github.germangb.engine.plugins.assimp.lwjgl

import com.github.germangb.engine.animation.AnimationTimeline
import com.github.germangb.engine.animation.PositionKey
import com.github.germangb.engine.animation.RotationKey
import com.github.germangb.engine.animation.ScaleKey
import com.github.germangb.engine.assets.AssetManager
import com.github.germangb.engine.assets.assets
import com.github.germangb.engine.core.Context
import com.github.germangb.engine.files.FileHandle
import com.github.germangb.engine.framework.AAB
import com.github.germangb.engine.framework.Actor
import com.github.germangb.engine.framework.components.*
import com.github.germangb.engine.framework.materials.DiffuseMaterial
import com.github.germangb.engine.framework.materials.Material
import com.github.germangb.engine.graphics.*
import com.github.germangb.engine.graphics.TexelFormat.RGBA8
import com.github.germangb.engine.graphics.TextureFilter.LINEAR
import com.github.germangb.engine.graphics.VertexAttribute.*
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Quaternion
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.plugins.assimp.*
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*
import org.lwjgl.system.jemalloc.JEmalloc.je_free
import org.lwjgl.system.jemalloc.JEmalloc.je_malloc

/**
 * Load animation data
 */
fun loadAIAnimation(anim: AIAnimation): AnimationData {
//    val scene = aiImportFile(file.path, aiProcessPreset_TargetRealtime_Fast) ?: return null
//    val anim = AIAnimation.create(scene.mAnimations()[0])

    val fps = anim.mTicksPerSecond()
    val duration = anim.mDuration().toInt()
    val timelines = mutableMapOf<String, AnimationTimeline>()

    // dump keyframes
    (0 until anim.mNumChannels())
            .map { AINodeAnim.create(anim.mChannels()[it]) }
            .forEachIndexed { i, ch ->
                val node = ch.mNodeName().dataString()

                // timeline
                val pos = mutableListOf<PositionKey>()
                val rot = mutableListOf<RotationKey>()
                val sca = mutableListOf<ScaleKey>()

                // dump rotation keys
                (0 until ch.mNumRotationKeys())
                        .map { ch.mRotationKeys()[it] }
                        .forEachIndexed { index, it ->
                            val time = it.mTime()
                            val quat = it.mValue()
                            val trans = ch.mPositionKeys()[index].mValue()

                            pos.add(PositionKey(time.toFloat(), Vector3(trans.x(), trans.y(), trans.z())))
                            rot.add(RotationKey(time.toFloat(), Quaternion(quat.x(), quat.y(), quat.z(), quat.w())))
                            sca.add(ScaleKey(time.toFloat(), Vector3(1f)))
                        }
                //println("node:$node")
                timelines[node] = AnimationTimeline(rot, pos, sca)
            }

    //aiFreeScene(scene)
    return AnimationData(duration, fps.toInt(), timelines)
}

/**
 * Load actor
 */
fun loadActor(ctx: Context, file: FileHandle, manager: AssetManager, flags: Int): AssimpSceneData? {
    val aiFlags = aiProcess_Triangulate or
            aiProcess_GenUVCoords or
            aiProcess_GenSmoothNormals or
            aiProcess_LimitBoneWeights or
            aiProcess_FlipUVs

    val scene = aiImportFile(file.path, aiFlags) ?: return null

    // Load bones

    val bones = mutableMapOf<String, Pair<Int, Matrix4c>>()
    (0 until scene.mNumMeshes())
            .map { AIMesh.create(scene.mMeshes()[it]) }
            .forEach { mesh ->
                println("${file.path} ${mesh.mNumFaces()}")
                (0 until mesh.mNumBones())
                        .map { AIBone.create(mesh.mBones()[it]) }
                        .forEach {
                            val id = bones.size
                            bones[it.mName().dataString()] = id to Matrix4().set(it.mOffsetMatrix())
                        }
            }

    // Load animations

    val numAnims = scene.mNumAnimations()
    val animations = if (flags and ANIMATIONS != 0) {
        List(numAnims) {
            val anim = AIAnimation.create(scene.mAnimations()[it])
            loadAIAnimation(anim)
        }
    } else {
        emptyList()
    }

    // Load meshes

    val meshes = if (flags and MESHES != 0) {
        List(scene.mNumMeshes()) {
            val aimesh = AIMesh.create(scene.mMeshes()[it])
            val attrs = arrayOf(POSITION, NORMAL, UV, JOINT_IDS, JOINT_WEIGHTS)
            val mesh = aiMeshToGL(aimesh, attrs, ctx.graphics, bones)
            val meshPath = "${file.path}/mesh_$it"
            manager.delegateMesh(mesh, meshPath)
            mesh to aimesh.mNumBones()
        }
    } else {
        emptyList()
    }

    // Load materials

    // Build node blueprint
    val blueprint = if (flags and SCENE != 0) {

        // load materials
        val materials = List(scene.mNumMeshes()) {
            val mat = DiffuseMaterial()
            val textureFile = ctx.files.getLocal("hellknight.png")
            ctx.assets.loadTexture(textureFile, RGBA8, LINEAR, LINEAR)?.let {
                manager.delegateTexture(it, "hellknight.png?$it")
                mat.diffuse = it
            }
            mat
        }

        // load nodes
        val node = GCNode(scene.mRootNode())
        node.convert(manager, meshes, materials, bones)
    } else {
        {}
    }

    // free data
    aiFreeScene(scene)

    return AssimpSceneData(meshes.map { it.first }, animations, blueprint)
}

/**
 * Convert aiMesh bind engine mesh
 */
fun aiMeshToGL(mesh: AIMesh, attributes: Array<out VertexAttribute>, gfx: GraphicsDevice, boneIds: Map<String, Pair<Int, Matrix4c>> = emptyMap()): Mesh {
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
    val glMesh = gfx.createMesh(vertexData, indexData, MeshPrimitive.TRIANGLES, MeshUsage.STATIC, *attributes)

    // free resources
    vertexData.clear()
    indexData.clear()
    je_free(vertexData)
    je_free(indexData)
    return glMesh
}

/**
 * Convert aiNode bind a kotlin node
 */
class GCNode(val aiNode: AINode) {
    /** Child nodes */
    val children: List<GCNode> = List(aiNode.mNumChildren()) {
        val aichild = AINode.create(aiNode.mChildren()[it])
        GCNode(aichild)
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
     * Convert hierarchy bind blueprint
     */
    fun convert(manager: AssetManager, meshes: List<Pair<Mesh, Int>>, materials: List<Material>, bones: Map<String, Pair<Int, Matrix4c>>): Actor.() -> Unit = {
        // set local transform
        transform.local.set(this@GCNode.transform)
        name = this@GCNode.name

        // add joint component
        bones[name]?.let { (id, offset) ->
            addJoint(id, offset)
        }

        // add children
        this@GCNode.children.forEach {
            addChild(it.convert(manager, meshes, materials, bones))
        }

        // armature root
        val armRoot = this

        // add meshes
        this@GCNode.meshes.forEach {
            addChild {
                // static mesh
                if (meshes[it].second == 0) {
                    //val mat = Material()
                    //mat.setTexture("diffuse", materials[it])
                    addMeshInstancer(meshes[it].first, materials[it])
                    addChild {
                        addMeshInstance()
                    }
                } else {
                    //val mat = Material()
                    //mat.setTexture("diffuse", materials[it])
                    addSkinnedMeshInstancer(armRoot, meshes[it].first, materials[it])
                    addChild {
                        addSkinnedMeshInstance(AAB())
                    }
                }
            }
        }
    }
}

/** AI matrix bind joml */
private fun Matrix4.set(ai: AIMatrix4x4): Matrix4 {
    m00(ai.a1()); m01(ai.b1()); m02(ai.c1()); m03(ai.d1())
    m10(ai.a2()); m11(ai.b2()); m12(ai.c2()); m13(ai.d2())
    m20(ai.a3()); m21(ai.b3()); m22(ai.c3()); m23(ai.d3())
    m30(ai.a4()); m31(ai.b4()); m32(ai.c4()); m33(ai.d4())
    return this
}
