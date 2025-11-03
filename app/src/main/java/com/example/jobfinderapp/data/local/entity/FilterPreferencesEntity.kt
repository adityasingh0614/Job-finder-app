package com.example.jobfinderapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter_preferences")
data class FilterPreferencesEntity(
    @PrimaryKey
    val id: Int = 1, // Single row for user preferences
    val category: String = "",
    val jobType: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)