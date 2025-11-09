package com.example.jobfinderapp.presentation.jobdetails

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.CachePolicy
import com.example.jobfinderapp.domain.model.Job

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    jobId: Int,
    onBackClick: () -> Unit,
    viewModel: JobDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isBookmarked by viewModel.isBookmarked.collectAsState()

    LaunchedEffect(jobId) {
        viewModel.loadJobDetails(jobId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            if (uiState is JobDetailsUiState.Success) {
                val job = (uiState as JobDetailsUiState.Success).job
                ExtendedFloatingActionButton(
                    onClick = {
                        openUrlInCustomTabs(context, job.applyUrl)
                    },
                    icon = {
                        Icon(Icons.Default.OpenInBrowser, "Apply")
                    },
                    text = { Text("Apply Now") }
                )
            }
        },
        // ✅ Remove extra window insets
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        when (val state = uiState) {
            is JobDetailsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is JobDetailsUiState.Success -> {
                JobDetailsContent(
                    job = state.job,
                    isBookmarked = isBookmarked,
                    onBookmarkClick = { viewModel.toggleBookmark(state.job) },
                    onShareClick = { shareJob(context, state.job) },
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is JobDetailsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error loading job details")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadJobDetails(jobId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JobDetailsContent(
    job: Job,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Section with Company Logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            // Backdrop gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .blur(20.dp)
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // ✅ FIXED: Company Logo with Initials Fallback
                val fallbackLogoUrl = remember(job.companyName) {
                    val domain = job.companyName.replace(" ", "").lowercase()
                    "https://logo.clearbit.com/$domain.com"
                }

                var showInitials by remember { mutableStateOf(true) }

                Box(
                    modifier = Modifier.size(80.dp)
                ) {
                    // Show initials first
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(generateColorFromName(job.companyName)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getCompanyInitials(job.companyName),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Try to load actual logo
                    if (!showInitials) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(fallbackLogoUrl)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .listener(
                                    onSuccess = { _, _ ->
                                        showInitials = false
                                    }
                                )
                                .build(),
                            contentDescription = "Company logo",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Job Title
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Company Name
                Text(
                    text = job.companyName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Location & Salary Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = job.location,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (!job.salary.isNullOrEmpty()) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = job.salary,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalIconButton(onClick = onBookmarkClick) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark
                            else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalIconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                }
            }
        }

        // Job Details Sections
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Tags
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                job.tags.forEach { tag ->
                    AssistChip(
                        onClick = { },
                        label = { Text(tag) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Tag,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Job Type & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    icon = Icons.Default.Work,
                    label = "Job Type",
                    value = job.jobType
                )
                InfoItem(
                    icon = Icons.Default.Category,
                    label = "Category",
                    value = job.category
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About the Job
            SectionHeader(title = "About the Job", icon = Icons.Default.Description)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = remember(job.description) {
                    // Remove HTML tags and clean up the text
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(job.description, Html.FROM_HTML_MODE_LEGACY).toString()
                    } else {
                        Html.fromHtml(job.description).toString()
                    }
                        .replace("\n\n\n", "\n\n") // Remove excessive newlines
                        .trim()
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 29.sp // Better readability
            )


            Spacer(modifier = Modifier.height(120.dp)) // Space for FAB
        }
    }
}

// ✅ Add these helper functions
private fun getCompanyInitials(companyName: String): String {
    val words = companyName.split(" ", "-", ".", ",")
    return when {
        words.size >= 2 -> "${words[0].firstOrNull()?.uppercase()}${words[1].firstOrNull()?.uppercase()}"
        words.isNotEmpty() -> words[0].take(2).uppercase()
        else -> "CO"
    }
}

private fun generateColorFromName(name: String): Color {
    val hash = name.hashCode()
    val colors = listOf(
        Color(0xFF6750A4),
        Color(0xFF006A6A),
        Color(0xFF8E4585),
        Color(0xFF006D3B),
        Color(0xFF825500),
        Color(0xFF904D00),
        Color(0xFF006874),
    )
    return colors[Math.abs(hash) % colors.size]
}

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun openUrlInCustomTabs(context: android.content.Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}

private fun shareJob(context: android.content.Context, job: Job) {
    val shareIntent = Intent.createChooser(
        Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, job.title)
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this job: ${job.title} at ${job.companyName}\n\n${job.applyUrl}"
            )
        },
        "Share Job"
    )
    context.startActivity(shareIntent)
}
