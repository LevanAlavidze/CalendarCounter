package com.example.testforcalendarcounter.repository.settings

import com.example.testforcalendarcounter.data.UserSettings
import com.example.testforcalendarcounter.data.dao.UserSettingsDao
import javax.inject.Inject

class UserSettingsRepositoryImpl @Inject constructor(
    private val userSettingsDao: UserSettingsDao
) : UserSettingsRepository {

    override suspend fun getUserSettings(): UserSettings? {
        return userSettingsDao.getUserSettings()
    }

    override suspend fun updateBaselineCigsPerDay(baseline: Int) {
        val currentSettings = userSettingsDao.getUserSettings()
        // If no row yet, create. If row exists, update.
        val newSettings = currentSettings?.copy(baselineCigsPerDay = baseline)
            ?: UserSettings(id = 0, baselineCigsPerDay = baseline)
        userSettingsDao.insertOrUpdate(newSettings)
    }
    override suspend fun getLegacyBaseline(): Int {
        val settings = userSettingsDao.getUserSettings()
        return settings?.baselineCigsPerDay ?: DEFAULT_BASELINE
    }

    private companion object {
        private const val DEFAULT_BASELINE = 20 // <- pick your app’s real default
    }


}