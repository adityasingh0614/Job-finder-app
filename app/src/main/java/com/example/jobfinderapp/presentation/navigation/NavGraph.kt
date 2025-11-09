package com.example.jobfinderapp.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.jobfinderapp.presentation.home.HomeScreen
import com.example.jobfinderapp.presentation.jobdetails.JobDetailsScreen
import com.example.jobfinderapp.presentation.profile.ProfileScreen
import com.example.jobfinderapp.presentation.saved.SavedJobsScreen
import com.example.jobfinderapp.presentation.search.SearchScreen
import com.example.jobfinderapp.presentation.settings.SettingsScreen
import com.example.jobfinderapp.presentation.components.FilterBottomSheet
import com.yourname.jobfinder.presentation.home.HomeViewModel

@Composable
fun JobFinderNavigation() {
    val navController = rememberNavController()

    // ✅ ALWAYS show bottom nav - no hiding/showing
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Route.Home> {
                val viewModel: HomeViewModel = hiltViewModel()
                var showFilterSheet by remember { mutableStateOf(false) }

                HomeScreen(
                    onJobClick = { jobId ->
                        navController.navigate(Route.JobDetails(jobId))
                    },
                    onSearchClick = {
                        navController.navigate(Route.Search)
                    },
                    onFilterClick = {
                        showFilterSheet = true
                    },
                    viewModel = viewModel
                )

                if (showFilterSheet) {
                    FilterBottomSheet(
                        currentFilters = viewModel.currentFilter.collectAsState().value,
                        onDismiss = { showFilterSheet = false },
                        onApplyFilters = { filters ->
                            viewModel.updateFilter(filters)
                            showFilterSheet = false
                        }
                    )
                }
            }

            composable<Route.Search> {
                SearchScreen(
                    onBackClick = { navController.navigateUp() },
                    onJobClick = { jobId ->
                        navController.navigate(Route.JobDetails(jobId))
                    }
                )
            }

            composable<Route.Saved> {
                SavedJobsScreen(
                    onJobClick = { jobId ->
                        navController.navigate(Route.JobDetails(jobId))
                    }
                )
            }

            composable<Route.Profile> {
                ProfileScreen(
                    onSettingsClick = {
                        navController.navigate(Route.Settings)
                    }
                )
            }

            // ✅ Job Details also has bottom nav now
            composable<Route.JobDetails> { backStackEntry ->
                val jobDetails: Route.JobDetails = backStackEntry.toRoute()
                JobDetailsScreen(
                    jobId = jobDetails.jobId,
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable<Route.Settings> {
                SettingsScreen(
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}
