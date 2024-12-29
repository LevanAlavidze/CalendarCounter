package com.example.testforcalendarcounter

import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.dao.CigaretteDao
import com.example.testforcalendarcounter.data.dao.timer.Timer
import com.example.testforcalendarcounter.data.dao.timer.TimerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.*
import javax.inject.Inject


class CigaretteRepositoryImpl @Inject constructor(
    private val dao: CigaretteDao,
    private val timerDao: TimerDao
) : CigaretteRepository {

    override suspend fun addCigarette() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val entry = dao.getEntryByDate(today) ?: CigaretteEntry(timestamp = 0L, date = today, count = 0)
        dao.insertOrUpdate(entry.copy(count = entry.count + 1))
    }

    override suspend fun getDailyCount(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return dao.getEntryByDate(today)?.count ?: 0
    }

    override suspend fun getWeeklyCount(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfWeek = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
        return dao.getCountBetweenDates(startOfWeek, today)
    }

    override suspend fun getMonthlyCount(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfMonth = today.minus(today.dayOfMonth - 1, DateTimeUnit.DAY)
        return dao.getCountBetweenDates(startOfMonth, today)
    }
    override suspend fun saveTimer(timer: Timer) {
        timerDao.insertOrUpdate(timer)
    }
    override suspend fun getTimer(): Timer? = withContext(Dispatchers.IO) {
        timerDao.getTimer()
    }
}
