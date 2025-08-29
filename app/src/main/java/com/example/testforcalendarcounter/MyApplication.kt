package com.example.testforcalendarcounter

import android.app.Application
import com.example.testforcalendarcounter.repository.cigarette.BaselineRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject lateinit var baselineRepository: BaselineRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            // One-time backfill (safe to call repeatedly)
            baselineRepository.ensureBootstrapped()
        }
    }
}