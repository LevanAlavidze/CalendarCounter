package com.example.testforcalendarcounter.dataBase

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.testforcalendarcounter.data.DateConverter
import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.dao.CigaretteDao
import com.example.testforcalendarcounter.data.dao.timer.Timer
import com.example.testforcalendarcounter.data.dao.timer.TimerDao

@Database(
    entities = [CigaretteEntry::class, Timer::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(DateConverter::class)
abstract class CigaretteDatabase : RoomDatabase() {
    abstract fun cigaretteDao(): CigaretteDao
    abstract fun timerDao(): TimerDao
}