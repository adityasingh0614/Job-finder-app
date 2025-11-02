package com.example.jobfinderapp.domain.repository
import com.example.jobfinderapp.data.local.dao.JobDao
import com.example.jobfinderapp.data.local.entity.toEntity
import com.example.jobfinderapp.data.local.entity.toJob
import com.example.jobfinderapp.data.remote.api.JobApiService
import com.example.jobfinderapp.data.remote.dto.toDomain
import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.model.JobFilter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val apiService: JobApiService,
    private val jobDao: JobDao
) : JobRepository {

    override suspend fun searchJobs(filter: JobFilter): Result<List<Job>> {
        return try {
            val response = when {
                filter.searchQuery.isNotEmpty() -> {
                    apiService.getRemoteJobs(search = filter.searchQuery)
                }
                filter.category.isNotEmpty() -> {
                    apiService.getRemoteJobs(category = filter.category)
                }
                filter.companyName.isNotEmpty() -> {
                    apiService.getRemoteJobs(companyName = filter.companyName)
                }
                else -> {
                    apiService.getAllJobs()
                }
            }

            if (response.isSuccessful) {
                val jobs = response.body()?.jobs?.map { it.toDomain() } ?: emptyList()
                Result.success(jobs)
            } else {
                Result.failure(Exception("Failed to fetch jobs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getJobDetails(jobId: Int): Result<Job> {
        return try {
            // First check local database
            val localJob = jobDao.getJobById(jobId)
            if (localJob != null) {
                return Result.success(localJob.toJob())
            }

            // If not found locally, fetch from API
            val response = apiService.getAllJobs()
            if (response.isSuccessful) {
                val job = response.body()?.jobs
                    ?.find { it.id == jobId }
                    ?.toDomain()

                if (job != null) {
                    Result.success(job)
                } else {
                    Result.failure(Exception("Job not found"))
                }
            } else {
                Result.failure(Exception("Failed to fetch job details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSavedJobs(): Flow<List<Job>> {
        return jobDao.getAllJobs().map { entities ->
            entities.map { it.toJob() }
        }
    }

    override suspend fun saveJob(job: Job): Result<Unit> {
        return try {
            jobDao.insertJob(job.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeSavedJob(jobId: Int): Result<Unit> {
        return try {
            jobDao.deleteJobById(jobId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isJobSaved(jobId: Int): Flow<Boolean> {
        return jobDao.isJobSaved(jobId)
    }
}
