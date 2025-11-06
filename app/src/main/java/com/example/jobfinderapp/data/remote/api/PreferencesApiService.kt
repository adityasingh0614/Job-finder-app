package com.example.jobfinderapp.data.remote.api
import com.example.jobfinderapp.data.remote.dto.PreferenceRequestDto
import com.example.jobfinderapp.data.remote.dto.PreferenceResponseDto
import com.example.jobfinderapp.data.remote.dto.UserPreferencesResponseDto
import retrofit2.Response
import retrofit2.http.*

interface PreferencesApiService {

    @POST("preferences")
    suspend fun savePreference(
        @Body request: PreferenceRequestDto
    ): Response<PreferenceResponseDto>

    @GET("preferences")
    suspend fun getUserPreferences(
        @Query("userId") userId: String
    ): Response<UserPreferencesResponseDto>

    @DELETE("preferences/{preferenceId}")
    suspend fun deletePreference(
        @Path("preferenceId") preferenceId: String
    ): Response<Unit>
}
