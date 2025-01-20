package com.example.testforcalendarcounter.dataBase

import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase

class MyAutoMigrationSpec : AutoMigrationSpec {
    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        // Explicitly create the table if it doesn't exist
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `user_settings` (
                `id` INTEGER NOT NULL PRIMARY KEY,
                `baselineCigsPerDay` INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
    }
}
