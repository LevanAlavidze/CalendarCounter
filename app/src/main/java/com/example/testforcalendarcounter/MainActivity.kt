package com.example.testforcalendarcounter


import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.testforcalendarcounter.data.quote.shakeDetector.ShakeDetector
import com.example.testforcalendarcounter.databinding.ActivityMainBinding
import com.example.testforcalendarcounter.quote.showRandomMotivationalQuoteDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    @Inject lateinit var shakeDetector: ShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyBackgroundFromPreferences()

        // Set up navigation.
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            val gamesDestinations = setOf(
                R.id.gamesFragment, R.id.memoryGameFragment,
                R.id.tapGameFragment, R.id.breathingTimerFragment,
                R.id.quizFragment
            )
            if (destination.id in gamesDestinations) {
                binding.bottomNavigation.menu.findItem(R.id.gamesFragment).isChecked = true
            }
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        shakeDetector.shakeListener = object : ShakeDetector.OnShakeListener {
            override fun onShake() {
                // When a shake is detected, show or update the motivational quote dialog.
                showRandomMotivationalQuoteDialog()
            }
        }


    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_UI)
        }
        //applyBackgroundFromPreferences()

    }

    fun applyBackgroundFromPreferences() {
        val prefs = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val type = prefs.getString("background_type", "default") ?: "default"
        val value = prefs.getString("background_value", "#FFFFFF") ?: "#FFFFFF"

        // Update the root view background
        when (type) {
            "default" -> binding.root.setBackgroundResource(0) // or any default resource
            "solid" -> {
                try {
                    val colorInt = Color.parseColor(value) // e.g. "#FF0000"
                    binding.root.setBackgroundColor(colorInt)
                } catch (e: Exception) {
                    binding.root.setBackgroundColor(Color.WHITE)
                }
            }
            "gradient" -> {
                // value should be the drawable name, e.g. "gradient_bg_1"
                val resId = resources.getIdentifier(value, "drawable", packageName)
                if (resId != 0) {
                    binding.root.setBackgroundResource(resId)
                } else {
                    binding.root.setBackgroundColor(Color.WHITE)
                }
            }
        }
    }





    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(shakeDetector)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
