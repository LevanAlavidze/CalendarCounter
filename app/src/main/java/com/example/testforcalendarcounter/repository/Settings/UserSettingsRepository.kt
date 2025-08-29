package com.example.testforcalendarcounter.repository.settings

import com.example.testforcalendarcounter.data.UserSettings

interface UserSettingsRepository {
    suspend fun getUserSettings(): UserSettings?
    suspend fun updateBaselineCigsPerDay(baseline: Int)
    suspend fun getLegacyBaseline(): Int

}