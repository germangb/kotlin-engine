package com.github.germangb.player

enum class LookState {
    /** Agent looking north */
    NORTH,

    /** Agent looking south */
    SOUTH,

    /** Agent looking east */
    EAST,

    /** Playing looking west */
    WEST
}

enum class WalkState {
    /** Agent is not moving */
    STOPPED,

    /** Running state */
    RUNNING,

    /** Playing is walking at normal pace */
    WALKING
}

enum class CrouchState {
    /** Agent is standing */
    STANDING,

    /** Agent is crouching */
    CROUCHING
}