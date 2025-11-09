package com.example.jobfinderapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PreferenceRequestDto(
    @SerializedName("userId")
    val userId: String,

    @SerializedName("fcmToken")
    val fcmToken: String,

    @SerializedName("enableAlerts")
    val enableAlerts: Boolean,

    @SerializedName("alertFrequency")
    val alertFrequency: String,

    @SerializedName("jobTypes")
    val jobTypes: List<String>
)

data class FilterDto(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("jobType")
    val jobType: String = "",
    @SerializedName("location")
    val location: String = ""
)

data class PreferenceResponseDto(
    @SerializedName("message")
    val message: String,

    @SerializedName("preferenceId")
    val preferenceId: String? = null
)

data class UserPreferencesResponseDto(
    @SerializedName("preferences")
    val preferences: List<PreferenceRequestDto>
)

data class PreferenceItemDto(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("preferenceId")
    val preferenceId: String,
    @SerializedName("jobId")
    val jobId: String,
    @SerializedName("filters")
    val filters: FilterDto,
    @SerializedName("fcmToken")
    val fcmToken: String,
    @SerializedName("createdAt")
    val createdAt: String
)
