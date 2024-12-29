package com.example.testforcalendarcounter

import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.dao.timer.Timer

interface CigaretteRepository {
    suspend fun addCigarette()
    suspend fun getDailyCount(): Int
    suspend fun getWeeklyCount(): Int
    suspend fun getMonthlyCount(): Int
    //timer
    suspend fun saveTimer(timer: Timer)
    suspend fun getTimer(): Timer?
    //RV
    suspend fun deleteCigarette(entry: CigaretteEntry)
    suspend fun getLastTenCigarettes(): List<CigaretteEntry>
}
