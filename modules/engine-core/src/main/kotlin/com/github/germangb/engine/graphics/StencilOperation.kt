package com.github.germangb.engine.graphics

enum class StencilOperation {
    /** Increment */
    INCREMENT,

    /** increment with wrapping */
    INCREMENT_WRAP,

    /** decrement */
    DECREMENT,

    /** decrement with wrapping */
    DECREMENT_WRAP,

    /** Set to zero */
    ZERO,

    /** Keep value */
    KEEP,

    /** ~ value */
    NEGATE,

    /** Replace value */
    REPLACE
}