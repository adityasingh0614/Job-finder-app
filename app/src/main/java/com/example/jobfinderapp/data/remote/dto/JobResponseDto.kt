package com.example.jobfinderapp.data.remote.dto
import com.google.gson.annotations.SerializedName

data class JobResponseDto(
    @SerializedName("0-legal-notice")
    val legalNotice: String,
    @SerializedName("job-count")
    val jobCount: Int,
    @SerializedName("jobs")
    val jobs: List<JobDto>
)

data class JobDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("company_name")
    val companyName: String,
    @SerializedName("company_logo")
    val companyLogo: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("job_type")
    val jobType: String?,
    @SerializedName("publication_date")
    val publicationDate: String,
    @SerializedName("candidate_required_location")
    val location: String,
    @SerializedName("salary")
    val salary: String?,
    @SerializedName("description")
    val description: String
)
