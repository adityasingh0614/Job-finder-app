package com.example.jobfinderapp.domain.usecase

import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.repository.JobRepository
import javax.inject.Inject

class GetJobDetailsUseCase @Inject constructor(
    private val repository: JobRepository
) {
    suspend operator fun invoke(jobId: Int): Result<Job> {
        return repository.getJobDetails(jobId)
    }
}