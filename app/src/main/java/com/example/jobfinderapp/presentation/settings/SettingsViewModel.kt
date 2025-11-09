package com.example.jobfinderapp.presentation.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobfinderapp.data.remote.api.PreferencesApiService
import com.example.jobfinderapp.data.remote.dto.PreferenceRequestDto
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class JobPreferences(
    val enableAlerts: Boolean = false,
    val alertFrequency: String = "Daily",
    val jobTypes: Set<String> = emptySet()
)

sealed class SavePreferencesState {
    object Idle : SavePreferencesState()
    object Loading : SavePreferencesState()
    object Success : SavePreferencesState()
    data class Error(val message: String) : SavePreferencesState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesApiService: PreferencesApiService // ✅ Use your existing API
) : ViewModel() {

    private val _preferences = MutableStateFlow(JobPreferences())
    val preferences: StateFlow<JobPreferences> = _preferences.asStateFlow()

    private val _saveState = MutableStateFlow<SavePreferencesState>(SavePreferencesState.Idle)
    val saveState: StateFlow<SavePreferencesState> = _saveState.asStateFlow()

    fun updateEnableAlerts(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(enableAlerts = enabled)
    }

    fun updateAlertFrequency(frequency: String) {
        _preferences.value = _preferences.value.copy(alertFrequency = frequency)
    }

    fun updateJobTypes(jobTypes: Set<String>) {
        _preferences.value = _preferences.value.copy(jobTypes = jobTypes)
    }

    fun savePreferences() {
        viewModelScope.launch {
            try {
                _saveState.value = SavePreferencesState.Loading

                // Get FCM token
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                Log.d("SettingsViewModel", "FCM Token: $fcmToken")

                // Create request DTO
                val request = PreferenceRequestDto(
                    userId = fcmToken, // Use FCM token as userId
                    fcmToken = fcmToken,
                    enableAlerts = _preferences.value.enableAlerts,
                    alertFrequency = _preferences.value.alertFrequency,
                    jobTypes = _preferences.value.jobTypes.toList()
                )

                Log.d("SettingsViewModel", "Saving preferences: $request")

                // ✅ Call your existing API
                val response = preferencesApiService.savePreference(request)

                if (response.isSuccessful) {
                    Log.d("SettingsViewModel", "✅ Preferences saved successfully!")
                    _saveState.value = SavePreferencesState.Success
                    kotlinx.coroutines.delay(2000)
                    _saveState.value = SavePreferencesState.Idle
                } else {
                    Log.e("SettingsViewModel", "❌ Error: ${response.code()}")
                    _saveState.value = SavePreferencesState.Error(
                        "Failed to save: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                Log.e("SettingsViewModel", "❌ Exception", e)
                _saveState.value = SavePreferencesState.Error(
                    e.message ?: "Failed to save preferences"
                )
            }
        }
    }
}
