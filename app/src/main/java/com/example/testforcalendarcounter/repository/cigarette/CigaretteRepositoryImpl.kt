package com.example.testforcalendarcounter.repository.cigarette

import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.dao.CigaretteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import javax.inject.Inject

class CigaretteRepositoryImpl @Inject constructor(
    private val cigaretteDao: CigaretteDao,
) : CigaretteRepository {

    override suspend fun addCigarette() = withContext(Dispatchers.IO) {
        val now = Clock.System.now()
        val timestamp = now.toEpochMilliseconds()
        val date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val newEntry = CigaretteEntry(timestamp = timestamp, date = date, count = 1)
        cigaretteDao.insertOrUpdate(newEntry)
    }

    override suspend fun deleteCigarette(entry: CigaretteEntry) = withContext(Dispatchers.IO) {
        cigaretteDao.deleteCigarette(entry)
    }

    override suspend fun getLastTenCigarettes(): List<CigaretteEntry> = withContext(Dispatchers.IO) {
        cigaretteDao.getLastTenCigarettes()
    }

    // Counts
    override suspend fun getDailyCount(): Int = withContext(Dispatchers.IO) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        cigaretteDao.getDailySum(today) ?: 0
    }

    override suspend fun getWeeklyCount(): Int = withContext(Dispatchers.IO) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfWeek = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
        cigaretteDao.getCountBetweenDates(startOfWeek, today)
    }

    override suspend fun getMonthlyCount(): Int = withContext(Dispatchers.IO) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfMonth = today.minus(today.dayOfMonth - 1, DateTimeUnit.DAY)
        cigaretteDao.getCountBetweenDates(startOfMonth, today)
    }

    override suspend fun getCountBetweenDates(
        startDate: LocalDate,
        endDate: LocalDate
    ): Int = withContext(Dispatchers.IO) {
        cigaretteDao.getCountBetweenDates(startDate, endDate)
    }
    override suspend fun getCountBetweenDatesList(
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, Int> = withContext(Dispatchers.IO) {
        // If your DAO returns a list of DateCount(date: LocalDate, totalCount: Int)
        val rawList = cigaretteDao.getCountsGroupedByDate(startDate, endDate)
        // Convert List<DateCount> -> Map<LocalDate, Int>
        rawList.associate { it.date to it.totalCount }
    }

}