package com.example.jobfinderapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jobfinderapp.data.local.entity.FilterPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterPreferencesDao {

    @Query("SELECT * FROM filter_preferences WHERE id = 1")
    fun getFilterPreferences(): Flow<FilterPreferencesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFilterPreferences(preferences: FilterPreferencesEntity)

    @Query("DELETE FROM filter_preferences")
    suspend fun clearPreferences()
}