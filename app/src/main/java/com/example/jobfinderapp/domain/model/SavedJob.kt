package com.example.jobfinderapp.domain.model

data class SavedJob(
    val jobId: Int,
    val job: Job,
    val savedAt: Long = System.currentTimeMillis()
)
