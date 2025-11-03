package com.example.jobfinderapp.presentation.saved

import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.repository.JobRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedJobsViewModel @Inject constructor(
    private val repository: JobRepository
) : ViewModel() {

    val savedJobs: StateFlow<List<Job>> = repository.getSavedJobs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeJob(job: Job) {
        viewModelScope.launch {
            repository.removeSavedJob(job.id)
        }
    }
}
