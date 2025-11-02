package com.example.jobfinderapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jobfinderapp.data.local.dao.JobDao
import com.example.jobfinderapp.data.local.entity.JobEntity

@Database(
    entities = [JobEntity::class], version = 1, exportSchema = false
)
abstract class JobDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
}
