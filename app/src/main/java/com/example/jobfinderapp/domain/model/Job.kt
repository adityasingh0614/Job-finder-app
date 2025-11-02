package com.example.jobfinderapp.domain.model

data class Job(
    val id: Int,
    val title: String,
    val companyName: String,
    val companyLogo: String,
    val location: String,
    val salary: String?,
    val description: String,
    val category: String,
    val jobType: String,
    val tags: List<String>,
    val postedDate: String,
    val applyUrl: String
)
