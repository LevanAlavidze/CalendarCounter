package com.example.testforcalendarcounter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 0,
    val baselineCigsPerDay: Int = 0
)