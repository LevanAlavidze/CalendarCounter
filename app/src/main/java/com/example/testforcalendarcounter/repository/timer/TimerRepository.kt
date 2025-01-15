package com.example.testforcalendarcounter.repository.timer

import com.example.testforcalendarcounter.data.timer.Timer

interface TimerRepository {
    suspend fun saveTimer(timer: Timer)
    suspend fun getTimer(): Timer?
}