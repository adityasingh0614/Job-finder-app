package com.example.jobfinderapp.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.model.JobFilter
import com.example.jobfinderapp.domain.usecase.GetSavedJobsUseCase
import com.example.jobfinderapp.domain.usecase.SaveJobUseCase
import com.example.jobfinderapp.domain.usecase.SearchJobsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchJobsUseCase: SearchJobsUseCase,
    private val saveJobUseCase: SaveJobUseCase,
    private val getSavedJobsUseCase: GetSavedJobsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _savedJobIds = MutableStateFlow<Set<Int>>(emptySet())
    val savedJobIds: StateFlow<Set<Int>> = _savedJobIds.asStateFlow()

    init {
        loadJobs()
        observeSavedJobs()
    }

    fun loadJobs(filter: JobFilter = JobFilter()) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            searchJobsUseCase(filter).fold(
                onSuccess = { jobs ->
                    _uiState.value = if (jobs.isEmpty()) {
                        HomeUiState.Empty
                    } else {
                        HomeUiState.Success(jobs)
                    }
                },
                onFailure = { error ->
                    _uiState.value = HomeUiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    fun toggleBookmark(job: Job) {
        viewModelScope.launch {
            saveJobUseCase(job).fold(
                onSuccess = {
                    // Success handled by Flow observation
                },
                onFailure = { error ->
                    // Handle error (show snackbar, etc.)
                }
            )
        }
    }

    fun refresh() {
        loadJobs()
    }

    private fun observeSavedJobs() {
        viewModelScope.launch {
            getSavedJobsUseCase().collect { savedJobs ->
                _savedJobIds.value = savedJobs.map { it.id }.toSet()
            }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val jobs: List<Job>) : HomeUiState()
    object Empty : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
