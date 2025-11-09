package com.example.jobfinderapp.presentation.common.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobCard(
    title: String,
    companyName: String,
    location: String,
    salary: String,
    companyLogo: String,
    tags: List<String>,
    isBookmarked: Boolean,
    onCardClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    // ✅ Generate fallback logo URL using Clearbit or company domain
    val fallbackLogoUrl = remember(companyName) {
        // Try to extract domain from company name or use Clearbit API
        val domain = extractDomain(companyName)
        "https://logo.clearbit.com/$domain"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onCardClick()
            },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ✅ Use fallback logo service since Remotive blocks direct access
            var showInitials by remember { mutableStateOf(false) }

            if (showInitials) {
                // Show company initials if image fails
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(generateColorFromName(companyName)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getCompanyInitials(companyName),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            } else {
                // ✅ Optimized AsyncImage with better configuration
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(fallbackLogoUrl)
                        .crossfade(200) // Faster crossfade animation
                        .size(48.dp.value.toInt()) // Specify exact size for faster loading
                        .memoryCachePolicy(CachePolicy.ENABLED) // Enable memory cache
                        .diskCachePolicy(CachePolicy.ENABLED) // Enable disk cache
                        .networkCachePolicy(CachePolicy.ENABLED) // Enable network cache
                        .listener(
                            onStart = {
                                // Show initials immediately while loading
                                showInitials = true
                            },
                            onSuccess = { _, _ ->
                                // Hide initials when image loads
                                showInitials = false
                            },
                            onError = { _, _ ->
                                // Keep showing initials on error
                                showInitials = true
                                Log.d("JobCard", "Using initials for: $companyName")
                            }
                        )
                        .build(),
                    contentDescription = "Company logo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentScale = ContentScale.Crop
                )

            }

            Spacer(modifier = Modifier.width(12.dp))

            // Rest of your JobCard code stays the same...
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = companyName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer
                                )
                            ),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = salary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tags.take(3).forEach { tag ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onBookmarkClick()
                }
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Bookmark
                    else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ✅ Helper function to extract domain from company name
private fun extractDomain(companyName: String): String {
    return when {
        companyName.contains("Healthcare", ignoreCase = true) -> "${companyName.replace(" ", "").lowercase()}.com"
        else -> "${companyName.replace(" ", "").lowercase()}.com"
    }
}

// ✅ Helper function to get company initials
private fun getCompanyInitials(companyName: String): String {
    val words = companyName.split(" ", "-", ".", ",")
    return when {
        words.size >= 2 -> "${words[0].firstOrNull()?.uppercase()}${words[1].firstOrNull()?.uppercase()}"
        words.isNotEmpty() -> words[0].take(2).uppercase()
        else -> "CO"
    }
}

// ✅ Generate color from company name for consistent branding
private fun generateColorFromName(name: String): Color {
    val hash = name.hashCode()
    val colors = listOf(
        Color(0xFF6750A4), // Purple
        Color(0xFF006A6A), // Teal
        Color(0xFF8E4585), // Pink
        Color(0xFF006D3B), // Green
        Color(0xFF825500), // Orange
        Color(0xFF006A6A), // Cyan
        Color(0xFF904D00), // Brown
        Color(0xFF006874), // Blue
    )
    return colors[Math.abs(hash) % colors.size]
}
