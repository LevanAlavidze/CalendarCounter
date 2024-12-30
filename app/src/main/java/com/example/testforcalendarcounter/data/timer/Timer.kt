package com.example.testforcalendarcounter.data.timer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer")
data class Timer(
    @PrimaryKey val id: Int = 0,
    val startTime: Long,
    val isRunning: Boolean
)
