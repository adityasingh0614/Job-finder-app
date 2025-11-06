package com.example.jobfinderapp.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Home : Route

    @Serializable
    data object Search : Route

    @Serializable
    data object Saved : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data class JobDetails(val jobId: Int) : Route

    @Serializable
    data object Settings : Route
}
