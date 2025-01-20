package com.example.testforcalendarcounter.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testforcalendarcounter.data.UserSettings

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM user_settings WHERE id = 0 LIMIT 1")
    suspend fun getUserSettings(): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(userSettings: UserSettings)
}