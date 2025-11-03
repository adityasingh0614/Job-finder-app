package com.example.jobfinderapp.presentation.search


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobfinderapp.data.local.dao.SearchHistoryDao
import com.example.jobfinderapp.data.local.entity.SearchHistoryEntity
import com.example.jobfinderapp.domain.model.Job
import com.example.jobfinderapp.domain.model.JobFilter
import com.example.jobfinderapp.domain.usecase.SearchJobsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchJobsUseCase: SearchJobsUseCase,
    private val searchHistoryDao: SearchHistoryDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Job>>(emptyList())
    val searchResults: StateFlow<List<Job>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    val recentSearches = searchHistoryDao.getRecentSearches()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        setupDebouncedSearch()
    }

    private fun setupDebouncedSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Wait 300ms after user stops typing
                .distinctUntilChanged()
                .filter { it.isNotEmpty() }
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearchTriggered(query: String) {
        if (query.isNotEmpty()) {
            viewModelScope.launch {
                searchHistoryDao.insertSearch(
                    SearchHistoryEntity(searchQuery = query)
                )
            }
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isSearching.value = true

            val filter = JobFilter(searchQuery = query)
            searchJobsUseCase(filter).fold(
                onSuccess = { jobs ->
                    _searchResults.value = jobs
                },
                onFailure = {
                    _searchResults.value = emptyList()
                }
            )

            _isSearching.value = false
        }
    }

    fun deleteSearchHistory(search: SearchHistoryEntity) {
        viewModelScope.launch {
            searchHistoryDao.deleteSearch(search)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryDao.clearAllSearches()
        }
    }
}
