package com.example.jobfinderapp.domain.usecase

import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.repository.JobRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetSavedJobsUseCase @Inject constructor(
    private val repository: JobRepository
) {
    operator fun invoke(): Flow<List<Job>> {
        return repository.getSavedJobs()
    }
}