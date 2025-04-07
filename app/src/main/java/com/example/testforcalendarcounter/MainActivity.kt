package com.example.testforcalendarcounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.testforcalendarcounter.data.quote.shakeDetector.ShakeDetector
import com.example.testforcalendarcounter.data.quote.showRandomMotivationalQuote
import com.example.testforcalendarcounter.databinding.ActivityMainBinding
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


        //set up Navigation Component
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)



        navController.addOnDestinationChangedListener{_, destination, _ ->
            val gamesDestination = setOf(R.id.gamesFragment, R.id.memoryGameFragment, R.id.tapGameFragment, R.id.breathingTimerFragment)
            if (destination.id in gamesDestination){
                binding.bottomNavigation.menu.findItem(R.id.gamesFragment).isChecked = true
            }
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        shakeDetector.shakeListener = object : ShakeDetector.OnShakeListener{
            override fun onShake() {
                showRandomMotivationalQuote()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(shakeDetector)
    }
}