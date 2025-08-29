package com.example.testforcalendarcounter.data.entity

import androidx.room.DeleteTable
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase

@DeleteTable.Entries(
    DeleteTable(tableName = "lifetime_stats") // keep only if it's removed in v4
)
class V3to4FixSpec : AutoMigrationSpec {
    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        // Repair broken/empty baseline_history: drop and recreate with the exact schema
        db.execSQL("DROP TABLE IF EXISTS baseline_history")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS baseline_history (
                effectiveDate TEXT NOT NULL,
                baseline INTEGER NOT NULL,
                PRIMARY KEY(effectiveDate)
            )
            """.trimIndent()
        )

        // Defensive: ensure user_settings exists
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS user_settings (
                id INTEGER NOT NULL,
                baselineCigsPerDay INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
    }
}
