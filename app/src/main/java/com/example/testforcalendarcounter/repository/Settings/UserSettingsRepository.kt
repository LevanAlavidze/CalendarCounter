package com.example.testforcalendarcounter.repository.Settings

import com.example.testforcalendarcounter.data.UserSettings

interface UserSettingsRepository {
    suspend fun getUserSettings(): UserSettings?
    suspend fun updateBaselineCigsPerDay(baseline: Int)

}