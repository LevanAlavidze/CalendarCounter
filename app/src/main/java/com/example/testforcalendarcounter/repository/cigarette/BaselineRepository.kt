package com.example.testforcalendarcounter.repository.cigarette

import com.example.testforcalendarcounter.data.entity.BaselineHistory
import kotlinx.datetime.LocalDate

interface BaselineRepository {
    suspend fun setBaselineEffective(date: LocalDate, baseline: Int)
    suspend fun baselineFor(date: LocalDate): Int
    suspend fun getAll(): List<BaselineHistory>
    suspend fun earliestEffective(): LocalDate?

}