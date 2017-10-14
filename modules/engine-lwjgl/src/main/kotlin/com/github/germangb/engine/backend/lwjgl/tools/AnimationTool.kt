package com.github.germangb.engine.backend.lwjgl.tools

import org.lwjgl.assimp.AIAnimation
import org.lwjgl.assimp.AINodeAnim
import org.lwjgl.assimp.Assimp.*
import java.io.FileOutputStream
import java.io.PrintStream

fun BAIL(): Nothing = throw Exception("BAILING OOOOUT! some error, whatever I don't give a shit")

fun main(args: Array<String>) {
    val file = "walk7"
    val scene = aiImportFile("$file.md5anim", aiProcessPreset_TargetRealtime_Fast) ?: BAIL()
    val anim = AIAnimation.create(scene.mAnimations()[0])
    //println("# Animations = ${scene.mNumAnimations()}")
    //println("# Channels = ${anim.mNumChannels()}")
    //println("Duration = ${anim.mDuration()}f")

    System.setOut(PrintStream(FileOutputStream("$file.txt")))
    System.err.println(anim.mDuration())

    // dump keyframes
    (0 until anim.mNumChannels())
            .map { AINodeAnim.create(anim.mChannels()[it]) }
            .forEachIndexed { i, ch ->
                val node = ch.mNodeName().dataString()
                // dump rotation keys
                (0 until ch.mNumRotationKeys())
                        .map { ch.mRotationKeys()[it] }
                        .forEachIndexed { index, it ->
                            val time = it.mTime()
                            val quat = it.mValue()
                            val trans = ch.mPositionKeys()[index].mValue()
                            println("${time/anim.mDuration()}|${quat.x()}|${quat.y()}|${quat.z()}|${quat.w()}|${trans.x()}|${trans.y()}|${trans.z()}")
                        }
                println("node:$node")
            }

    aiFreeScene(scene)
}