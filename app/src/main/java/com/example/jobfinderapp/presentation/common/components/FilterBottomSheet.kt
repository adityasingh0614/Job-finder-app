package com.example.jobfinderapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jobfinderapp.domain.model.JobFilter
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    currentFilters: JobFilter = JobFilter(), // ✅ Accept current filters
    onDismiss: () -> Unit,
    onApplyFilters: (JobFilter) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // ✅ Initialize with current filters
    var selectedCategory by remember { mutableStateOf(currentFilters.category) }
    var selectedJobType by remember { mutableStateOf(currentFilters.jobType) }

    val categories = listOf(
        "All Categories",
        "Software Development",
        "Design",
        "Marketing",
        "Customer Support",
        "Sales",
        "Product",
        "Data Science"
    )

    val jobTypes = listOf(
        "Full-time",
        "Part-time",
        "Contract",
        "Freelance"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Section
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val displayCategory = if (category == "All Categories") "" else category
                    FilterChip(
                        selected = selectedCategory == displayCategory,
                        onClick = { selectedCategory = displayCategory },
                        label = { Text(category) },
                        leadingIcon = if (selectedCategory == displayCategory) {
                            { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Job Type Section
            Text(
                text = "Job Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                jobTypes.forEach { jobType ->
                    FilterChip(
                        selected = selectedJobType == jobType,
                        onClick = { selectedJobType = jobType },
                        label = { Text(jobType) },
                        leadingIcon = if (selectedJobType == jobType) {
                            { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedCategory = ""
                        selectedJobType = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }

                Button(
                    onClick = {
                        // ✅ Apply filters with proper structure
                        onApplyFilters(
                            JobFilter(
                                category = selectedCategory,
                                jobType = selectedJobType,
                                searchQuery = ""
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

