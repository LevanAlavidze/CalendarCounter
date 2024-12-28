package com.example.testforcalendarcounter

import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.dao.CigaretteDao
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.*
import javax.inject.Inject

class CigaretteRepository @Inject constructor(
    private val dao: CigaretteDao
            ) {
    suspend fun addCigarette() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val entry = dao.getEntryByDate(today) ?: CigaretteEntry(date = today, count = 0)
        dao.insertOrUpdate(entry.copy(count = entry.count + 1))
    }

    suspend fun getDailyCount(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return dao.getEntryByDate(today)?.count ?: 0
    }

    suspend fun getWeeklyCount(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfWeek = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
        return dao.getCountBetweenDates(startOfWeek, today)
    }

    suspend fun getMonthlyCount(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfMonth = today.minus(today.dayOfMonth - 1, DateTimeUnit.DAY)
        return dao.getCountBetweenDates(startOfMonth, today)
    }
}
