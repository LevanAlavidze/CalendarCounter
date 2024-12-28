package com.example.testforcalendarcounter.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.testforcalendarcounter.data.DateConverter
import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.dao.CigaretteDao

@Database(entities = [CigaretteEntry::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class CigaretteDatabase: RoomDatabase() {
    abstract fun cigaretteDao(): CigaretteDao
}