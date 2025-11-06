package com.example.jobfinderapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobfinderapp.domain.usecase.GetSavedJobsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getSavedJobsUseCase: GetSavedJobsUseCase
) : ViewModel() {

    val savedJobsCount: StateFlow<Int> = getSavedJobsUseCase()
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val applicationsCount: StateFlow<Int> = flowOf(0) // TODO: Implement applications tracking
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
}
