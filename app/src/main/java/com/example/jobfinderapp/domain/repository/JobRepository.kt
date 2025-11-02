package com.example.jobfinderapp.domain.repository

import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.model.JobFilter
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    suspend fun searchJobs(filter: JobFilter): Result<List<Job>>
    suspend fun getJobDetails(jobId: Int): Result<Job>
    fun getSavedJobs(): Flow<List<Job>>
    suspend fun saveJob(job: Job): Result<Unit>
    suspend fun removeSavedJob(jobId: Int): Result<Unit>
    suspend fun isJobSaved(jobId: Int): Flow<Boolean>


}