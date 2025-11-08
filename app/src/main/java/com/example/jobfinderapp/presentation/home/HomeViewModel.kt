package com.yourname.jobfinder.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.jobfinderapp.data.paging.JobsPagingSource
import com.example.jobfinderapp.data.remote.api.JobApiService
import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.model.JobFilter
import com.example.jobfinderapp.domain.usecase.GetSavedJobsUseCase
import com.example.jobfinderapp.domain.usecase.SaveJobUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: JobApiService,
    private val saveJobUseCase: SaveJobUseCase,
    private val getSavedJobsUseCase: GetSavedJobsUseCase
) : ViewModel() {

    private val _currentFilter = MutableStateFlow(JobFilter())
    val currentFilter: StateFlow<JobFilter> = _currentFilter.asStateFlow()

    val jobsPagingFlow: Flow<PagingData<Job>> = _currentFilter
        .flatMapLatest { filter ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false,
                    prefetchDistance = 3
                ),
                pagingSourceFactory = {
                    JobsPagingSource(apiService, filter)
                }
            ).flow
        }
        .cachedIn(viewModelScope)

    private val _savedJobIds = MutableStateFlow<Set<Int>>(emptySet())
    val savedJobIds: StateFlow<Set<Int>> = _savedJobIds.asStateFlow()

    init {
        observeSavedJobs()
    }

    fun updateFilter(filter: JobFilter) {
        _currentFilter.value = filter
    }

    fun clearFilters() {
        _currentFilter.value = JobFilter()
    }


    fun toggleBookmark(job: Job) {
        viewModelScope.launch {
            saveJobUseCase(job)
        }
    }

    private fun observeSavedJobs() {
        viewModelScope.launch {
            getSavedJobsUseCase().collect { savedJobs ->
                _savedJobIds.value = savedJobs.map { it.id }.toSet()
            }
        }
    }
}
