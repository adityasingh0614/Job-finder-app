package com.example.jobfinderapp.domain.model

data class JobFilter(
    val keyword: String = "",
    val category: String = "",
    val companyName: String = "",
    val jobType: String = "",
    val searchQuery: String = ""
)
