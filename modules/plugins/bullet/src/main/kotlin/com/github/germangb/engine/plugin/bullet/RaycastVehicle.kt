package com.github.germangb.engine.plugin.bullet

import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Vector3c

/**
 * RaycastVehice tuning parameters
 */
class VehicleTuning {
    var suspensionStiffness = 5.88f
    var suspensionCompression = 0.83f
    var suspensionDamping = 0.88f
    var frictionSlip = 10.5f
    var maxSuspensionForce = 6000f
}

/**
 * Wheel information
 */
interface WheelInfo {
    /** Wheel world transform */
    val worldTransform: Matrix4c

    /** Chasis connection point */
    val chassisConnectionPointCS: Vector3c

    /** Direction of the wheel */
    val wheelDirectionCS: Vector3c

    /** Radius of wheel */
    val wheelRadius: Float

    /** Suspension stiffness */
    val suspensionStiffness: Float

    /** Wheel steering */
    val steering: Float

    /** Wheel rotation */
    val rotation: Float

    /** Wheel brake */
    val brake: Float

    /** Is this a front wheel? */
    val isFrontWheel: Boolean
}

/**
 * Simulates a vehicle using simple & cheap model
 */
interface RaycastVehicle {
    /** The world transform of the car chassis */
    val chassisWorldTransform: Matrix4c

    /** Returns the chasis rigid body */
    val rigidBody: RigidBody

    /** Add a wheel to the vehicle simulation */
    fun addWheel(connectionPointCS0: Vector3c, wheelDirectionCS0: Vector3c, wheelAxle: Vector3c, suspensionRestLength: Float, wheelRadius: Float, tuning: VehicleTuning, isFrontWeel: Boolean): WheelInfo

    /** Applies brake to a wheel */
    fun setBrake(brake: Float, wheelIndex: Int)

    /** Calls setPitchControl function */
    fun setPitchControl(pitch: Float)

    /** Steers a specific wheel */
    fun setSteeringValue(steering: Float, wheelIndex: Int)

    /** Resets suspension */
    fun resetSuspension()
}