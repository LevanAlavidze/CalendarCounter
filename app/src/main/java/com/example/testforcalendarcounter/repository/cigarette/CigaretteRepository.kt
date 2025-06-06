package com.example.testforcalendarcounter.repository.cigarette

import com.example.testforcalendarcounter.data.CigaretteEntry

import kotlinx.datetime.LocalDate

interface CigaretteRepository {
    suspend fun addCigarette()
    suspend fun deleteCigarette(entry: CigaretteEntry)
    suspend fun getLastTenCigarettes(): List<CigaretteEntry>

    // Basic counts
    suspend fun getDailyCount(): Int
    suspend fun getWeeklyCount(): Int
    suspend fun getMonthlyCount(): Int

    suspend fun getCountBetweenDates(
        startDate: LocalDate,
        endDate: LocalDate
    ): Int

    suspend fun getCountBetweenDatesList(
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, Int>
}
