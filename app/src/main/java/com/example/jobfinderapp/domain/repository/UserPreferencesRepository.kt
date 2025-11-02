package com.example.jobfinderapp.domain.repository


import com.example.jobfinderapp.domain.model.JobFilter
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    suspend fun saveFilters(filters: JobFilter)
    fun getFilters(): Flow<JobFilter>
    suspend fun clearFilters()
}
