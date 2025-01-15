package com.example.testforcalendarcounter.repository.stats

import com.example.testforcalendarcounter.statistics.viewmodel.DayData
import com.example.testforcalendarcounter.statistics.viewmodel.MonthData

interface StatsRepository {
    suspend fun calculateDailyCost(): Pair<Int, Double>
    suspend fun calculateWeeklyCost(): Pair<Int, Double>
    suspend fun calculateMonthlyCost(): Pair<Int, Double>
    suspend fun calculateYearlyCost(): Pair<Int, Double>

    suspend fun getWeeklyStatsForLast7Days(): List<DayData>
    suspend fun getMonthlyStatsForThisMonth(): List<DayData>
    suspend fun getYearlyStatsForThisYear(): List<MonthData>
}