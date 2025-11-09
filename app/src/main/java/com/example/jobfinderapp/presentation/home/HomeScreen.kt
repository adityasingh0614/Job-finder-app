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
import androidx.compose.ui.graphics.Color
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
    onFilterClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val jobsPagingItems = viewModel.jobsPagingFlow.collectAsLazyPagingItems()
    val savedJobIds by viewModel.savedJobIds.collectAsState()

    // ✅ Fix overlapping by removing Scaffold topBar and adding TopBar manually
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TopBar outside Scaffold
        // ✅ Fixed TopBar - not collapsing
        HomeTopBar(
            onSearchClick = onSearchClick,
            onFilterClick = onFilterClick
        )

        // Jobs List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

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
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    // ✅ Use Surface instead of Box to avoid issues
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(), // Safe area for status bar
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
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

            // ✅ Use Card instead of SearchBar for better control
            Card(
                onClick = onSearchClick,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Search jobs, companies, skills...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onFilterClick) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filters",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


