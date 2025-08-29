package com.example.testforcalendarcounter.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testforcalendarcounter.data.entity.CigaretteEntry
import kotlinx.datetime.LocalDate


@Dao
interface CigaretteDao {
    @Query("SELECT * FROM CigaretteEntry WHERE date = :date")
    suspend fun getEntryByDate(date: LocalDate): CigaretteEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: CigaretteEntry)

    @Query("SELECT SUM(count) FROM CigaretteEntry WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getCountBetweenDates(startDate: LocalDate, endDate: LocalDate): Int

    @Query("SELECT SUM(count) FROM CigaretteEntry WHERE date = :date")
    suspend fun getDailySum(date: LocalDate): Int


    @Query("SELECT * FROM CigaretteEntry ORDER BY timestamp DESC LIMIT 10")
    suspend fun getLastTenCigarettes(): List<CigaretteEntry>

    @Delete
    suspend fun deleteCigarette(entry: CigaretteEntry)

    @Query("SELECT MIN(date) FROM cigaretteentry")
    suspend fun getEarliestDate(): LocalDate?


    @Query("""
        SELECT date, SUM(count) as totalCount
        FROM cigaretteentry
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date ASC
    """)
    suspend fun getCountsGroupedByDate(startDate: LocalDate, endDate: LocalDate): List<DateCount>

    @Query("""
        SELECT strftime('%m', date) as monthNum, SUM(count) as totalCount
        FROM cigaretteentry
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY monthNum
        ORDER BY monthNum
    """)
    suspend fun getCountsGroupedByMonth(startDate: LocalDate, endDate: LocalDate): List<MonthCount>



}

data class DateCount(
    val date: LocalDate,
    val totalCount: Int
)

data class MonthCount(
    val monthNum: String,  // Because strftime returns string
    val totalCount: Int
)