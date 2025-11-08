package com.example.jobfinderapp.presentation.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jobfinderapp.presentation.common.components.JobCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedJobsScreen(
    onJobClick: (Int) -> Unit,
    onSavedCountChange: (Int) -> Unit = {}, // ✅ Add this parameter with default value
    viewModel: SavedJobsViewModel = hiltViewModel()
) {
    val savedJobs by viewModel.savedJobs.collectAsState()

    // ✅ Update count when saved jobs change
    LaunchedEffect(savedJobs.size) {
        onSavedCountChange(savedJobs.size)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Saved Jobs",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${savedJobs.size} jobs saved",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (savedJobs.isEmpty()) {
            EmptySavedJobs(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = savedJobs,
                    key = { it.id }
                ) { job ->
                    JobCard(
                        title = job.title,
                        companyName = job.companyName,
                        location = job.location,
                        salary = job.salary ?: "Not specified",
                        companyLogo = job.companyLogo,
                        tags = job.tags,
                        isBookmarked = true,
                        onCardClick = { onJobClick(job.id) },
                        onBookmarkClick = { viewModel.removeJob(job) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySavedJobs(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "No saved jobs yet",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start bookmarking jobs you're interested in",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
