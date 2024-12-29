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
        val now = Clock.System.now()
        val timestamp = now.toEpochMilliseconds()
        val date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        // Always insert a new row with count=1 for each cigarette
        val newEntry = CigaretteEntry(
            timestamp = timestamp,
            date = date,
            count = 1
        )
        dao.insertOrUpdate(newEntry)
    }

    /*override suspend fun addCigarette() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val entry = dao.getEntryByDate(today) ?: CigaretteEntry(timestamp = 0L, date = today, count = 0)
        dao.insertOrUpdate(entry.copy(count = entry.count + 1))
    }*/

   /* override suspend fun addCigarette() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val existingEntry = dao.getEntryByDate(today)
        val newCount = (existingEntry?.count ?: 0) + 1

        // If you'd prefer multiple rows per day, do:
        // val entry = CigaretteEntry(
        //     timestamp = System.currentTimeMillis(),
        //     date = today,
        //     count = 1
        // )
        // dao.insertOrUpdate(entry)

        val entry = existingEntry?.copy(
            count = newCount,
            timestamp = System.currentTimeMillis()
        ) ?: CigaretteEntry(
            timestamp = System.currentTimeMillis(),
            date = today,
            count = 1
        )
        dao.insertOrUpdate(entry)
    }*/

    override suspend fun getDailyCount(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return dao.getDailySum(today) ?: 0  // If null, return 0
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
    override suspend fun deleteCigarette(entry: CigaretteEntry) {
        dao.deleteCigarette(entry)
    }

    override suspend fun getLastTenCigarettes(): List<CigaretteEntry> {
        return dao.getLastTenCigarettes()
    }
}
