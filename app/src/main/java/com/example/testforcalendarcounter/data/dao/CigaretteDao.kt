package com.example.testforcalendarcounter.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testforcalendarcounter.data.CigaretteEntry
import kotlinx.datetime.LocalDate


@Dao
interface CigaretteDao {
    @Query("SELECT * FROM CigaretteEntry WHERE date = :date")
    suspend fun getEntryByDate(date: LocalDate): CigaretteEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: CigaretteEntry)

    @Query("SELECT SUM(count) FROM CigaretteEntry WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getCountBetweenDates(startDate: LocalDate, endDate: LocalDate): Int
}