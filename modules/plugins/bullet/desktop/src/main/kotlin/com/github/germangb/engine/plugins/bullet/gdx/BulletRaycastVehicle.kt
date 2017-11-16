package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle
import com.badlogic.gdx.physics.bullet.dynamics.btWheelInfo
import com.github.germangb.engine.math.Matrix4
import com.github.germangb.engine.math.Matrix4c
import com.github.germangb.engine.math.Vector3
import com.github.germangb.engine.math.Vector3c
import com.github.germangb.engine.plugin.bullet.RaycastVehicle
import com.github.germangb.engine.plugin.bullet.RigidBody
import com.github.germangb.engine.plugin.bullet.VehicleTuning
import com.github.germangb.engine.plugin.bullet.WheelInfo

/**
 * Implement wheel info
 */
class BulletWheelInfo(val info: btWheelInfo) : WheelInfo {
    companion object {
        val aux0 = Matrix4()
        val aux1 = Vector3()
        val aux2 = GdxMatrix4()
    }

    override val worldTransform: Matrix4c
        get() {
            val trans = info.worldTransform
            trans.getOpenGLMatrix(aux2.values)
            return aux0.set(aux2)
        }

    override val chassisConnectionPointCS: Vector3c
        get() {
            val trans = info.chassisConnectionPointCS
            return aux1.set(trans.x, trans.y, trans.z)
        }

    override val wheelDirectionCS: Vector3c
        get() {
            val trans = info.wheelDirectionCS
            return aux1.set(trans.x, trans.y, trans.z)
        }

    override val wheelRadius get() = info.wheelsRadius
    override val suspensionStiffness get() = info.suspensionStiffness
    override val steering get() = info.steering
    override val rotation get() = info.rotation
    override val brake get() = info.brake
    override val isFrontWheel get() = info.bIsFrontWheel

}

/**
 * Implement raycast vehicle
 */
class BulletRaycastVehicle(override val rigidBody: RigidBody, val vehicle: btRaycastVehicle) : RaycastVehicle {
    companion object {
        val aux0 = Matrix4()
        val aux1 = GdxVector3()
        val aux2 = GdxVector3()
    }

    override val chassisWorldTransform get() = aux0.set(vehicle.chassisWorldTransform)

    override fun addWheel(connectionPointCS0: Vector3c, wheelDirectionCS0: Vector3c, wheelAxle: Vector3c, suspensionRestLength: Float, wheelRadius: Float, tuning: VehicleTuning, isFrontWeel: Boolean): WheelInfo {
        val btTuning = btRaycastVehicle.btVehicleTuning()
        btTuning.frictionSlip = tuning.frictionSlip
        btTuning.maxSuspensionForce = tuning.maxSuspensionForce
        btTuning.suspensionStiffness = tuning.suspensionStiffness
        btTuning.suspensionDamping = tuning.suspensionDamping
        btTuning.suspensionCompression = tuning.suspensionCompression
        return BulletWheelInfo(vehicle.addWheel(aux1, aux2, aux1, suspensionRestLength, wheelRadius, btTuning, isFrontWeel))
    }

    override fun setBrake(brake: Float, wheelIndex: Int) = vehicle.setBrake(brake, wheelIndex)
    override fun setPitchControl(pitch: Float) = vehicle.setPitchControl(pitch)
    override fun setSteeringValue(steering: Float, wheelIndex: Int) = vehicle.setSteeringValue(steering, wheelIndex)
    override fun resetSuspension() = vehicle.resetSuspension()
}
