package com.example.testforcalendarcounter.data.dao.timer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TimerDao {
    @Query("SELECT * FROM timer WHERE id = 0 LIMIT 1")
    suspend fun getTimer(): Timer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(timer: Timer)

    @Update
    suspend fun updateTimer(timer: Timer)
}
