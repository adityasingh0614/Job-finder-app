package com.example.jobfinderapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jobfinderapp.data.local.dao.FilterPreferencesDao
import com.example.jobfinderapp.data.local.dao.JobDao
import com.example.jobfinderapp.data.local.dao.SearchHistoryDao
import com.example.jobfinderapp.data.local.entity.FilterPreferencesEntity
import com.example.jobfinderapp.data.local.entity.JobEntity
import com.example.jobfinderapp.data.local.entity.SearchHistoryEntity

@Database(
    entities = [JobEntity::class,SearchHistoryEntity::class,
        FilterPreferencesEntity::class]
    , version = 2, exportSchema = false,

)
abstract class JobDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun searchHistoryDao(): SearchHistoryDao  // ADD THIS
    abstract fun filterPreferencesDao(): FilterPreferencesDao
}
