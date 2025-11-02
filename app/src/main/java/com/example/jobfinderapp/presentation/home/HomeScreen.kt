package com.example.jobfinderapp.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jobfinderapp.presentation.common.components.EmptyState
import com.example.jobfinderapp.presentation.common.components.ErrorState
import com.example.jobfinderapp.presentation.common.components.JobCard
import com.example.jobfinderapp.presentation.common.components.LoadingShimmer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onJobClick: (Int) -> Unit, onSearchClick: () -> Unit, viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedJobIds by viewModel.savedJobIds.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HomeTopBar(
                onSearchClick = onSearchClick
            )
        }) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing, onRefresh = {
                isRefreshing = true
                viewModel.refresh()
            }, modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LaunchedEffect(uiState) {
                if (uiState !is HomeUiState.Loading) {
                    isRefreshing = false
                }
            }

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    LoadingShimmer()
                }

                is HomeUiState.Success -> {
                    JobsList(
                        jobs = state.jobs,
                        savedJobIds = savedJobIds,
                        onJobClick = onJobClick,
                        onBookmarkClick = { job ->
                            viewModel.toggleBookmark(job)
                        })
                }

                is HomeUiState.Empty -> {
                    EmptyState(
                        message = "No jobs found", description = "Try adjusting your search filters"
                    )
                }

                is HomeUiState.Error -> {
                    ErrorState(
                        message = state.message, onRetryClick = { viewModel.refresh() })
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
                        imageVector = Icons.Default.Search, contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { /* Open filters */ }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList, contentDescription = "Filters"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {}
        }
    }
}

@Composable
private fun JobsList(
    jobs: List<com.example.jobfinderapp.domain.model.Job>,
    savedJobIds: Set<Int>,
    onJobClick: (Int) -> Unit,
    onBookmarkClick: (com.example.jobfinderapp.domain.model.Job) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(
            items = jobs, key = { job -> job.id }) { job ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                    animationSpec = tween(300)
                ),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                JobCard(
                    title = job.title,
                    companyName = job.companyName,
                    location = job.location,
                    salary = job.salary ?: "Not specified",
                    companyLogo = job.companyLogo,
                    tags = job.tags,
                    isBookmarked = savedJobIds.contains(job.id),
                    onCardClick = { onJobClick(job.id) },
                    onBookmarkClick = { onBookmarkClick(job) })
            }
        }
    }
}