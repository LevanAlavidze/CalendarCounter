package com.example.testforcalendarcounter.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.testforcalendarcounter.data.DateConverter
import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.dao.CigaretteDao
import com.example.testforcalendarcounter.data.dao.PackPriceDao
import com.example.testforcalendarcounter.data.timer.Timer
import com.example.testforcalendarcounter.data.dao.TimerDao
import com.example.testforcalendarcounter.data.packprice.PackPrice

@Database(
    entities = [CigaretteEntry::class, Timer::class, PackPrice::class],
    version = 1,
    exportSchema = true,
    /*autoMigrations = [
        AutoMigration(from = 2, to = 3)
    ]*/
)
@TypeConverters(DateConverter::class)
abstract class CigaretteDatabase : RoomDatabase() {
    abstract fun cigaretteDao(): CigaretteDao
    abstract fun timerDao(): TimerDao
    abstract fun packPriceDao(): PackPriceDao
}