package com.example.jobfinderapp.presentation.settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var jobAlertsEnabled by remember { mutableStateOf(true) }
    var alertFrequency by remember { mutableStateOf("Daily") }
    var selectedJobTypes by remember { mutableStateOf(setOf("Full-time")) }
    var preferredLocation by remember { mutableStateOf("Worldwide") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Job Alerts Section
            Text(
                text = "Job Alerts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Enable Job Alerts",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Get notified about new jobs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = jobAlertsEnabled,
                            onCheckedChange = { jobAlertsEnabled = it }
                        )
                    }

                    if (jobAlertsEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))

                        // Alert Frequency
                        Text(
                            text = "Alert Frequency",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Daily", "Weekly").forEach { frequency ->
                                FilterChip(
                                    selected = alertFrequency == frequency,
                                    onClick = { alertFrequency = frequency },
                                    label = { Text(frequency) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Preferred Job Types
                        Text(
                            text = "Preferred Job Types",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Full-time", "Part-time", "Contract", "Freelance").forEach { type ->
                                FilterChip(
                                    selected = type in selectedJobTypes,
                                    onClick = {
                                        selectedJobTypes = if (type in selectedJobTypes) {
                                            selectedJobTypes - type
                                        } else {
                                            selectedJobTypes + type
                                        }
                                    },
                                    label = { Text(type) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (jobAlertsEnabled) {
                        viewModel.savePreferences(
                            category = "",
                            jobTypes = selectedJobTypes,
                            location = preferredLocation,
                            frequency = alertFrequency
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is SettingsUiState.Loading
            ) {
                if (uiState is SettingsUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Preferences")
                }
            }

            // Show success/error message
            when (val state = uiState) {
                is SettingsUiState.Success -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✓ Preferences saved successfully!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is SettingsUiState.Error -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Error: ${state.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }
        }
    }
}
