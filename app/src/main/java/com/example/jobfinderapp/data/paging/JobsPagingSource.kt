package com.example.jobfinderapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.jobfinderapp.data.remote.api.JobApiService
import com.example.jobfinderapp.data.remote.dto.toDomain
import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.model.JobFilter

class JobsPagingSource(
    private val apiService: JobApiService,
    private val filter: JobFilter
) : PagingSource<Int, Job>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Job> {
        return try {
            val page = params.key ?: 1

            val response = when {
                filter.searchQuery.isNotEmpty() -> {
                    apiService.getRemoteJobs(search = filter.searchQuery)
                }
                filter.category.isNotEmpty() -> {
                    apiService.getRemoteJobs(category = filter.category)
                }
                else -> {
                    apiService.getAllJobs()
                }
            }

            if (response.isSuccessful) {
                val jobs = response.body()?.jobs?.map { it.toDomain() } ?: emptyList()

                // Simulate pagination (Remotive doesn't have native pagination)
                val startIndex = (page - 1) * params.loadSize
                val endIndex = (startIndex + params.loadSize).coerceAtMost(jobs.size)
                val pagedJobs = if (startIndex < jobs.size) {
                    jobs.subList(startIndex, endIndex)
                } else {
                    emptyList()
                }

                LoadResult.Page(
                    data = pagedJobs,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (pagedJobs.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("Failed to load jobs"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Job>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
