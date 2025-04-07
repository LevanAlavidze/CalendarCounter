package com.example.testforcalendarcounter.data.quote.shakeDetector


import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class ShakeDetector @Inject constructor() : SensorEventListener {

    var shakeListener: OnShakeListener? = null

    interface OnShakeListener {
        fun onShake()
    }

    // Adjust this threshold if needed.
    private val shakeThreshold = 25f
    private var lastShakeTime: Long = 0

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val acceleration = sqrt(x * x + y * y + z * z)
        if (acceleration > shakeThreshold) {
            val currentTime = System.currentTimeMillis()
            // Avoid triggering multiple times in rapid succession.
            if (currentTime - lastShakeTime > 2000) {
                lastShakeTime = currentTime
                shakeListener?.onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op.
    }
}
