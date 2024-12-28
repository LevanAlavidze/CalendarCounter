package com.example.testforcalendarcounter.data

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate

class DateConverter {
    @TypeConverter
    fun fromStringToDate(value: String): LocalDate = value.toLocalDate()

    @TypeConverter
    fun fromDateToString(date: LocalDate): String = date.toString()
}
