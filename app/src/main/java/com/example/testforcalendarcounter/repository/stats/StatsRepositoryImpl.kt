package com.example.testforcalendarcounter.repository.stats

import android.util.Log
import com.example.testforcalendarcounter.repository.cigarette.CigaretteRepository
import com.example.testforcalendarcounter.repository.packprice.PackPriceRepository
import com.example.testforcalendarcounter.statistics.viewmodel.DayData
import com.example.testforcalendarcounter.statistics.viewmodel.MonthData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*

import javax.inject.Inject

class StatsRepositoryImpl @Inject constructor(
    private val cigaretteRepository: CigaretteRepository,
    private val packPriceRepository: PackPriceRepository,
) : StatsRepository {

    // region Cost Calculations
    override suspend fun calculateDailyCost(): Pair<Int, Double> = withContext(Dispatchers.IO) {
        val dailyCount = cigaretteRepository.getDailyCount()
        val costPerCigarette = packPriceRepository.calculateCostPerCigarette()
        dailyCount to (dailyCount * costPerCigarette)
    }

    override suspend fun calculateWeeklyCost(): Pair<Int, Double> = withContext(Dispatchers.IO) {
        val weeklyCount = cigaretteRepository.getWeeklyCount()
        val costPerCigarette = packPriceRepository.calculateCostPerCigarette()
        weeklyCount to (weeklyCount * costPerCigarette)
    }

    override suspend fun calculateMonthlyCost(): Pair<Int, Double> = withContext(Dispatchers.IO) {
        val monthlyCount = cigaretteRepository.getMonthlyCount()
        val costPerCigarette = packPriceRepository.calculateCostPerCigarette()
        monthlyCount to (monthlyCount * costPerCigarette)
    }

    override suspend fun calculateYearlyCost(): Pair<Int, Double> = withContext(Dispatchers.IO) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        // Subtract (dayOfYear - 1) days => Jan 1 of this year
        val startOfYear = today.minus(today.dayOfYear - 1, DateTimeUnit.DAY)
        val yearlyCount = cigaretteRepository.getCountBetweenDates(startOfYear, today)
        val costPerCigarette = packPriceRepository.calculateCostPerCigarette()
        val yearlyCost = yearlyCount * costPerCigarette
        Log.d("StatsRepository", "Yearly: count=$yearlyCount, cost=$yearlyCost")
        yearlyCount to yearlyCost
    }
    // endregion

    // region Stats Over Time
    override suspend fun getWeeklyStatsForLast7Days(): List<DayData> = withContext(Dispatchers.IO) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val daysFromMonday = today.dayOfWeek.ordinal
        val startOfWeek = today.minus(daysFromMonday, DateTimeUnit.DAY)
        val endOfWeek = startOfWeek.plus(6, DateTimeUnit.DAY)

        // getCountBetweenDates for each day
        val dailyCounts = cigaretteRepository.getCountBetweenDatesList(startOfWeek, endOfWeek)
        // Or if you have a specialized DAO method that returns grouped data:
        //   val dailyCounts = cigaretteDao.getCountsGroupedByDate(startOfWeek, endOfWeek)

        val costPerCig = packPriceRepository.calculateCostPerCigarette()
        val dayDataList = mutableListOf<DayData>()
        for (i in 0..6) {
            val date = startOfWeek.plus(i, DateTimeUnit.DAY)
            val count = dailyCounts[date] ?: 0  // or if you parse the grouped data
            val cost = count * costPerCig
            dayDataList.add(
                DayData(
                    day = date.dayOfMonth,
                    count = count,
                    cost = cost
                )
            )
        }
        dayDataList
    }

    override suspend fun getMonthlyStatsForThisMonth(): List<DayData> = withContext(Dispatchers.IO) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfMonth = today.minus(today.dayOfMonth - 1, DateTimeUnit.DAY)
        val daysInMonth = startOfMonth.month.length(isLeapYear(startOfMonth.year))
        val endOfMonth = startOfMonth.plus(daysInMonth - 1, DateTimeUnit.DAY)

        // same logic: get daily sums from your DB
        val dailyCounts = cigaretteRepository.getCountBetweenDatesList(startOfMonth, endOfMonth)
        val costPerCig = packPriceRepository.calculateCostPerCigarette()
        val result = mutableListOf<DayData>()

        for (dayIndex in 1..daysInMonth) {
            val currentDate = startOfMonth.plus(dayIndex - 1, DateTimeUnit.DAY)
            val count = dailyCounts[currentDate] ?: 0
            val cost = count * costPerCig
            result.add(
                DayData(day = dayIndex, count = count, cost = cost)
            )
        }

        result
    }

    override suspend fun getYearlyStatsForThisYear(): List<MonthData> = withContext(Dispatchers.IO) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfYear = today.minus(today.dayOfYear - 1, DateTimeUnit.DAY)

        // Suppose you have a DAO method returning month -> sum
        val monthlyCounts = /* cigaretteDao.getCountsGroupedByMonth(startOfYear, today) */
            emptyList<Pair<String,Int>>() // placeholder

        val costPerCig = packPriceRepository.calculateCostPerCigarette()
        monthlyCounts.map { (monthStr, totalCount) ->
            val monthInt = monthStr.toIntOrNull() ?: 0
            MonthData(
                month = monthInt,
                count = totalCount,
                cost = totalCount * costPerCig
            )
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
    // endregion
}