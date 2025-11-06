package com.example.jobfinderapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobfinderapp.data.remote.api.PreferencesApiService
import com.example.jobfinderapp.data.remote.dto.FilterDto
import com.example.jobfinderapp.data.remote.dto.PreferenceRequestDto
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesApi: PreferencesApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun savePreferences(
        category: String,
        jobTypes: Set<String>,
        location: String,
        frequency: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = SettingsUiState.Loading

                // Get FCM token
                val fcmToken = FirebaseMessaging.getInstance().token.await()

                // Create preference request
                val request = PreferenceRequestDto(
                    userId = "user123", // Replace with actual user ID
                    jobId = null,
                    filters = FilterDto(
                        category = category,
                        jobType = jobTypes.joinToString(","),
                        location = location
                    ),
                    fcmToken = fcmToken
                )

                // Call API
                val response = preferencesApi.savePreference(request)

                if (response.isSuccessful) {
                    _uiState.value = SettingsUiState.Success
                } else {
                    _uiState.value = SettingsUiState.Error("Failed to save preferences")
                }

            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class SettingsUiState {
    object Idle : SettingsUiState()
    object Loading : SettingsUiState()
    object Success : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}
