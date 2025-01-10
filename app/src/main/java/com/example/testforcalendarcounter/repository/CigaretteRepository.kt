package com.example.testforcalendarcounter.repository

import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.packprice.PackPrice
import com.example.testforcalendarcounter.data.timer.Timer
import com.example.testforcalendarcounter.statistics.viewmodel.DayData
import com.example.testforcalendarcounter.statistics.viewmodel.MonthData

interface CigaretteRepository {
    // Counters
    suspend fun getDailyCount(): Int
    suspend fun getWeeklyCount(): Int
    suspend fun getMonthlyCount(): Int

    // Timer
    suspend fun saveTimer(timer: Timer)
    suspend fun getTimer(): Timer?

    // Recycler
    suspend fun addCigarette()
    suspend fun deleteCigarette(entry: CigaretteEntry)
    suspend fun getLastTenCigarettes(): List<CigaretteEntry>

    // Pack Price
    suspend fun getPackPrice(): PackPrice?
    suspend fun updatePackPrice(price: Double, currency: String)

    // Cost Calculations
    suspend fun calculateCostPerCigarette(): Double
    suspend fun calculateDailyCost(): Pair<Int, Double>
    suspend fun calculateWeeklyCost(): Pair<Int, Double>
    suspend fun calculateMonthlyCost(): Pair<Int, Double>
    suspend fun calculateYearlyCost(): Pair<Int, Double>

    suspend fun getWeeklyStatsForLast7Days(): List<DayData>
    suspend fun getMonthlyStatsForThisMonth(): List<DayData>
    suspend fun getYearlyStatsForThisYear(): List<MonthData>

}
