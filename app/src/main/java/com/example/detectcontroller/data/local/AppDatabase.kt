package com.example.detectcontroller.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.detectcontroller.data.local.locDTO.ErrorEntity
import com.example.detectcontroller.data.local.locDTO.EventServerEntity
import com.example.detectcontroller.data.local.locDTO.LastEventsServerEntity
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.local.locDTO.UiStateEntity

@Database(
    entities = [
        UiStateEntity::class,
        EventServerEntity::class,
        RegServerEntity::class,
        LastEventsServerEntity::class,
        ErrorEntity::class
    ],
    version = 4,
    exportSchema = true
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun uiStateDao(): UiStateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `ErrorEntity` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `errorCode` INTEGER NOT NULL,
                        `errorMessage` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `deviceId` TEXT
                    )
                """.trimIndent())
            }
        }
    }
}


