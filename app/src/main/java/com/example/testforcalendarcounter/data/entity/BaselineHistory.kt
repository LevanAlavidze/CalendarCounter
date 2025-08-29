package com.example.testforcalendarcounter.data.entity

import androidx.room.Entity
import kotlinx.datetime.LocalDate


@Entity(tableName = "baseline_history", primaryKeys = ["effectiveDate"])
data class BaselineHistory(
    val effectiveDate: LocalDate,
    val baseline: Int
)