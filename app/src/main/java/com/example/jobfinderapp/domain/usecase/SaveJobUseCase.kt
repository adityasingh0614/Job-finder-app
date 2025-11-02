package com.example.jobfinderapp.domain.usecase


import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.repository.JobRepository
import javax.inject.Inject

class SaveJobUseCase @Inject constructor(
    private val repository: JobRepository
) {
    suspend operator fun invoke(job: Job): Result<Unit> {
        return repository.saveJob(job)
    }
}
