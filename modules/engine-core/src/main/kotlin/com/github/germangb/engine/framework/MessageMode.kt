package com.github.germangb.engine.framework

enum class MessageMode(internal val read: Boolean, internal val send: Boolean) {
    /**
     * Ignore the message, and don't pass it along
     */
    IGNORE(false, false),

    /**
     * Ignore the message, but send it down the hierarchy
     */
    IGNORE_SEND(false, true),

    /**
     * Receive message and send it down he hierarchy
     */
    READ_SEND(true, true)
}