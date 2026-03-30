package com.example.snaplog.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PhotoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SnapLogDatabase: RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}