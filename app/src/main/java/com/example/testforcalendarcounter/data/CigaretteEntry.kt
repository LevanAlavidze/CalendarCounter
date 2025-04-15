package com.example.testforcalendarcounter.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity
data class CigaretteEntry (
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val timestamp: Long,
    val date: LocalDate,
    val count: Int,
)