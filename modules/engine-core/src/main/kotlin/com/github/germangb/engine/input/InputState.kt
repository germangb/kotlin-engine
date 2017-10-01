package com.github.germangb.engine.input

enum class InputState {
    /** Input thing has just been pressed */
    JUST_PRESSED,

    /** Input thing is being pressed */
    PRESSED,

    /** Input thing has just been released */
    JUST_RELEASED,

    /** Input thing is NOT being pressed */
    RELEASED
}