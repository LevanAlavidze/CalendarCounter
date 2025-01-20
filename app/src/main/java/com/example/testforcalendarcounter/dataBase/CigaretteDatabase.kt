package com.example.testforcalendarcounter.dataBase

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.testforcalendarcounter.data.DateConverter
import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.UserSettings
import com.example.testforcalendarcounter.data.dao.CigaretteDao
import com.example.testforcalendarcounter.data.dao.PackPriceDao
import com.example.testforcalendarcounter.data.timer.Timer
import com.example.testforcalendarcounter.data.dao.TimerDao
import com.example.testforcalendarcounter.data.dao.UserSettingsDao
import com.example.testforcalendarcounter.data.packprice.PackPrice

@Database(
    entities = [
        CigaretteEntry::class,
        Timer::class,
        PackPrice::class,
        UserSettings::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = MyAutoMigrationSpec::class)
    ]
)

@TypeConverters(DateConverter::class)
abstract class CigaretteDatabase : RoomDatabase() {
    abstract fun cigaretteDao(): CigaretteDao
    abstract fun timerDao(): TimerDao
    abstract fun packPriceDao(): PackPriceDao
    abstract fun userSettingsDao(): UserSettingsDao
}
