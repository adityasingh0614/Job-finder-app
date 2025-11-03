package com.example.jobfinderapp.presentation.search


data class FilterState(
    val selectedCategory: String = "",
    val selectedJobTypes: Set<String> = emptySet(),
    val selectedLocation: String = ""
)