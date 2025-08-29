package com.example.testforcalendarcounter.data.dao

import androidx.room.*
import com.example.testforcalendarcounter.data.entity.BaselineHistory
import kotlinx.datetime.LocalDate

@Dao
interface BaselineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: BaselineHistory)


    @Query("""
        SELECT * FROM baseline_history
        WHERE effectiveDate <= :date
        ORDER BY effectiveDate DESC
        LIMIT 1
    """)
    suspend fun getBaselineForDate(date: LocalDate): BaselineHistory?

    @Query("SELECT * FROM baseline_history ORDER BY effectiveDate ASC")
    suspend fun getAll(): List<BaselineHistory>

    @Query("SELECT MIN(effectiveDate) FROM baseline_history")
    suspend fun getEarliestEffective(): LocalDate?

    @Query("SELECT * FROM baseline_history ORDER BY effectiveDate ASC")
    suspend fun getAllOrdered(): List<BaselineHistory>

    @Query("SELECT COUNT(*) FROM baseline_history")
    suspend fun count(): Int

}
