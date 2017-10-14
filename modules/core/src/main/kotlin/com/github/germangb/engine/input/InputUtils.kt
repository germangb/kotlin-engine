package com.github.germangb.engine.input

import com.github.germangb.engine.input.InputState.*

/**
 * Check if keyboard key is pressed
 */
fun KeyboardKey.isJustPressed(input: InputDevice) = input.keyboard.getState(this) == JUST_PRESSED

/**
 * Check if keyboard key is pressed
 */
fun KeyboardKey.isJustReleased(input: InputDevice) = input.keyboard.getState(this) == JUST_RELEASED

/**
 * Check if keyboard key is pressed
 */
fun KeyboardKey.isPressed(input: InputDevice) = input.keyboard.getState(this) == PRESSED || input.keyboard.getState(this) == JUST_PRESSED

/**
 * Check if mouse button is pressed
 */
fun MouseButton.isJustPressed(input: InputDevice) = input.mouse.getState(this) == JUST_PRESSED

/**
 * Check if mouse button is released
 */
fun MouseButton.isJustReleased(input: InputDevice) = input.mouse.getState(this) == JUST_RELEASED

/**
 * Check if mouse button is pressed
 */
fun MouseButton.isPressed(input: InputDevice) = input.mouse.getState(this) == PRESSED || input.mouse.getState(this) == JUST_PRESSED
