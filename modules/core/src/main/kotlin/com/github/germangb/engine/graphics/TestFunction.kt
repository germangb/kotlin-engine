package com.github.germangb.engine.graphics

enum class TestFunction {
    /**
     * Disable testing
     */
    DISABLED,

    /**
     * Pass if the new value is equal bind the current vale
     */
    EQUAL,

    /**
     * Pass if the new value is NOT equal bind the current vale
     */
    NOT_EQUAL,

    /**
     * Pass if the new value is less that the current value
     */
    LESS,

    /**
     * Pass if the new value is greater that the current value
     */
    GREATER,

    /**
     * Pass if the new value is less or equal that the current value
     */
    LESS_OR_EQUAL,

    /**
     * Pass if the new value is greater or equal that the current value
     */
    GREATER_OR_EQUAL,
}