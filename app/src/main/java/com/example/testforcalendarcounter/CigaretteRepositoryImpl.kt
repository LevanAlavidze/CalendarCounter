package com.example.testforcalendarcounter

import android.util.Log
import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.dao.CigaretteDao
import com.example.testforcalendarcounter.data.dao.PackPriceDao
import com.example.testforcalendarcounter.data.dao.TimerDao
import com.example.testforcalendarcounter.data.packprice.PackPrice
import com.example.testforcalendarcounter.data.timer.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import javax.inject.Inject

class CigaretteRepositoryImpl @Inject constructor(
    private val cigaretteDao: CigaretteDao,
    private val timerDao: TimerDao,
    private val packPriceDao: PackPriceDao
) : CigaretteRepository {

    // Insert new row for each cigarette
    override suspend fun addCigarette() {
        val now = Clock.System.now()
        val timestamp = now.toEpochMilliseconds()
        val date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val newEntry = CigaretteEntry(timestamp = timestamp, date = date, count = 1)
        cigaretteDao.insertOrUpdate(newEntry)
    }

    override suspend fun deleteCigarette(entry: CigaretteEntry) {
        cigaretteDao.deleteCigarette(entry)
    }

    override suspend fun getLastTenCigarettes(): List<CigaretteEntry> {
        return cigaretteDao.getLastTenCigarettes()
    }

    override suspend fun getPackPrice(): PackPrice? {
        // Default: 0.0 USD if nothing stored
        return packPriceDao.getPackPrice() ?: PackPrice(price = 0.0, currency = "USD")
    }

    override suspend fun updatePackPrice(price: Double, currency: String) {
        val packPrice = PackPrice(price = price, currency = currency)
        packPriceDao.insertOrUpdatePackPrice(packPrice)
    }

    // Timers
    override suspend fun saveTimer(timer: Timer) {
        timerDao.insertOrUpdate(timer)
    }

    override suspend fun getTimer(): Timer? = withContext(Dispatchers.IO) {
        timerDao.getTimer()
    }

    // Counters
    override suspend fun getDailyCount(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return cigaretteDao.getDailySum(today) ?: 0
    }

    override suspend fun getWeeklyCount(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfWeek = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
        return cigaretteDao.getCountBetweenDates(startOfWeek, today)
    }

    override suspend fun getMonthlyCount(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfMonth = today.minus(today.dayOfMonth - 1, DateTimeUnit.DAY)
        return cigaretteDao.getCountBetweenDates(startOfMonth, today)
    }

    // Cost calculations
    override suspend fun calculateCostPerCigarette(): Double {
        val packPrice = packPriceDao.getPackPrice()
        return packPrice?.price?.div(20) ?: 0.0
    }

    override suspend fun calculateDailyCost(): Pair<Int, Double> {
        val dailyCount = getDailyCount()
        val costPerCigarette = calculateCostPerCigarette()
        return dailyCount to (dailyCount * costPerCigarette)
    }

    override suspend fun calculateWeeklyCost(): Pair<Int, Double> {
        val weeklyCount = getWeeklyCount()
        val costPerCigarette = calculateCostPerCigarette()
        return weeklyCount to (weeklyCount * costPerCigarette)
    }

    override suspend fun calculateMonthlyCost(): Pair<Int, Double> {
        val monthlyCount = getMonthlyCount()
        val costPerCigarette = calculateCostPerCigarette()
        return monthlyCount to (monthlyCount * costPerCigarette)
    }
}
