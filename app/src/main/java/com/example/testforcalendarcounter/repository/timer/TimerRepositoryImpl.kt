package com.example.testforcalendarcounter.repository.timer

import com.example.testforcalendarcounter.data.dao.TimerDao
import com.example.testforcalendarcounter.data.timer.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TimerRepositoryImpl @Inject constructor(
    private val timerDao: TimerDao
) : TimerRepository {

    override suspend fun saveTimer(timer: Timer) = withContext(Dispatchers.IO) {
        timerDao.insertOrUpdate(timer)
    }

    override suspend fun getTimer(): Timer? = withContext(Dispatchers.IO) {
        timerDao.getTimer()
    }
}