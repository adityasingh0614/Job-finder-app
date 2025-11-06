package com.example.jobfinderapp.presentation.jobdetails


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.usecase.GetJobDetailsUseCase
import com.example.jobfinderapp.domain.usecase.SaveJobUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailsViewModel @Inject constructor(
    private val getJobDetailsUseCase: GetJobDetailsUseCase,
    private val saveJobUseCase: SaveJobUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<JobDetailsUiState>(JobDetailsUiState.Loading)
    val uiState: StateFlow<JobDetailsUiState> = _uiState.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    fun loadJobDetails(jobId: Int) {
        viewModelScope.launch {
            _uiState.value = JobDetailsUiState.Loading

            getJobDetailsUseCase(jobId).fold(
                onSuccess = { job ->
                    _uiState.value = JobDetailsUiState.Success(job)
                },
                onFailure = { error ->
                    _uiState.value = JobDetailsUiState.Error(
                        error.message ?: "Failed to load job details"
                    )
                }
            )
        }
    }

    fun toggleBookmark(job: Job) {
        viewModelScope.launch {
            saveJobUseCase(job)
            _isBookmarked.value = !_isBookmarked.value
        }
    }
}

sealed class JobDetailsUiState {
    object Loading : JobDetailsUiState()
    data class Success(val job: Job) : JobDetailsUiState()
    data class Error(val message: String) : JobDetailsUiState()
}
