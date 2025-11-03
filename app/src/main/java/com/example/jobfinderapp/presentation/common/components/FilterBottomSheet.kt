@file:OptIn(ExperimentalLayoutApi::class)

package com.example.jobfinderapp.presentation.common.components

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilters: com.example.jobfinderapp.presentation.search.FilterState,
    onDismiss: () -> Unit,
    onApplyFilters: (com.example.jobfinderapp.presentation.search.FilterState) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    var selectedCategory by remember { mutableStateOf(currentFilters.selectedCategory) }
    var selectedJobTypes by remember { mutableStateOf(currentFilters.selectedJobTypes) }
    var selectedLocation by remember { mutableStateOf(currentFilters.selectedLocation) }

    val categories = listOf(
        "All Categories",
        "Software Development",
        "Design",
        "Marketing",
        "Customer Support",
        "Sales",
        "Product",
        "Data Science",
        "DevOps"
    )

    val jobTypes = listOf(
        "Full-time",
        "Part-time",
        "Contract",
        "Freelance",
        "Internship"
    )

    val locations = listOf(
        "Worldwide",
        "USA Only",
        "Europe Only",
        "Asia Only",
        "Remote - US Timezone",
        "Remote - EU Timezone"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "Filters",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Category Section
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (category == "All Categories") "" else category
                        },
                        label = { Text(category) },
                        leadingIcon = if (selectedCategory == category) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Job Type Section
            Text(
                text = "Job Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                jobTypes.forEach { jobType ->
                    FilterChip(
                        selected = jobType in selectedJobTypes,
                        onClick = {
                            selectedJobTypes = if (jobType in selectedJobTypes) {
                                selectedJobTypes - jobType
                            } else {
                                selectedJobTypes + jobType
                            }
                        },
                        label = { Text(jobType) },
                        leadingIcon = if (jobType in selectedJobTypes) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Location Section
            Text(
                text = "Location",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                locations.forEach { location ->
                    FilterChip(
                        selected = selectedLocation == location,
                        onClick = { selectedLocation = location },
                        label = { Text(location) },
                        leadingIcon = if (selectedLocation == location) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
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
                        selectedJobTypes = emptySet()
                        selectedLocation = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }

                Button(
                    onClick = {
                        onApplyFilters(
                            com.example.jobfinderapp.presentation.search.FilterState(
                                selectedCategory = selectedCategory,
                                selectedJobTypes = selectedJobTypes,
                                selectedLocation = selectedLocation
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply Filters")
                }
            }
        }
    }
}
