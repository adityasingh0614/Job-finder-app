package com.example.jobfinderapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.jobfinderapp.presentation.common.components.ErrorState
import com.example.jobfinderapp.presentation.common.components.JobCard
import com.example.jobfinderapp.presentation.common.components.LoadingShimmer
import com.yourname.jobfinder.presentation.home.HomeViewModel

@Composable
fun HomeScreen(
    onJobClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val jobsPagingItems = viewModel.jobsPagingFlow.collectAsLazyPagingItems()
    val savedJobIds by viewModel.savedJobIds.collectAsState()

    Scaffold(
        topBar = { HomeTopBar(onSearchClick = onSearchClick) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ✅ Jobs List with proper key
            items(
                count = jobsPagingItems.itemCount,
                key = { index ->
                    jobsPagingItems.peek(index)?.id ?: "item_$index"
                }
            ) { index ->
                val job = jobsPagingItems[index]

                job?.let {
                    JobCard(
                        title = it.title,
                        companyName = it.companyName,
                        location = it.location,
                        salary = it.salary ?: "Not specified",
                        companyLogo = it.companyLogo,
                        tags = it.tags,
                        isBookmarked = savedJobIds.contains(it.id),
                        onCardClick = { onJobClick(it.id) },
                        onBookmarkClick = { viewModel.toggleBookmark(it) }
                    )
                }
            }

            // Loading state for initial load
            when {
                jobsPagingItems.loadState.refresh is LoadState.Loading -> {
                    item(key = "loading_refresh") {
                        LoadingShimmer()
                    }
                }

                jobsPagingItems.loadState.append is LoadState.Loading -> {
                    item(key = "loading_append") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                jobsPagingItems.loadState.refresh is LoadState.Error -> {
                    val error = jobsPagingItems.loadState.refresh as LoadState.Error
                    item(key = "error") {
                        ErrorState(
                            message = error.error.message ?: "Unknown error",
                            onRetryClick = { jobsPagingItems.retry() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    onSearchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Find Your Dream Job",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Explore thousands of remote opportunities",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            SearchBar(
                query = "",
                onQueryChange = {},
                onSearch = {},
                active = false,
                onActiveChange = { if (it) onSearchClick() },
                placeholder = {
                    Text("Search jobs, companies, skills...")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { /* Open filters */ }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filters"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {}
        }
    }
}
