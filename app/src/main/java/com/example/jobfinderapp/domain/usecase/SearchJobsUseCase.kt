package com.example.jobfinderapp.domain.usecase

import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.model.JobFilter
import com.example.jobfinderapp.domain.repository.JobRepository
import javax.inject.Inject

class SearchJobsUseCase @Inject constructor(
    private val repository: JobRepository
) {
    suspend operator fun invoke(filter: JobFilter): Result<List<Job>> {
        return repository.searchJobs(filter)
    }
}
