package com.example.jobfinderapp.data.remote.api

import com.example.jobfinderapp.data.remote.dto.JobResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface JobApiService {

    @GET("remote-jobs")
    suspend fun getRemoteJobs(
        @Query("category") category: String? = null,
        @Query("company_name") companyName: String? = null,
        @Query("search") search: String? = null,
        @Query("limit") limit: Int? = null
    ): retrofit2.Response<JobResponseDto>

    @GET("remote-jobs")
    suspend fun getAllJobs(): Response<JobResponseDto>
}