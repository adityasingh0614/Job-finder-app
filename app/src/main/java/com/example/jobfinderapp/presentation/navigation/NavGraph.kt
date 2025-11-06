package com.example.jobfinderapp.presentation.navigation


import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.jobfinderapp.presentation.home.HomeScreen
import com.example.jobfinderapp.presentation.jobdetails.JobDetailsScreen
import com.example.jobfinderapp.presentation.profile.ProfileScreen
import com.example.jobfinderapp.presentation.saved.SavedJobsScreen
import com.example.jobfinderapp.presentation.search.SearchScreen
import com.example.jobfinderapp.presentation.settings.SettingsScreen

@Composable
fun JobFinderNavigation() {
    val navController = rememberNavController()
    var savedJobsCount by remember { mutableStateOf(0) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Determine if bottom bar should be visible
    val showBottomBar = when (navBackStackEntry?.destination?.route?.substringBefore("?")) {
        Route.Home::class.qualifiedName,
        Route.Search::class.qualifiedName,
        Route.Saved::class.qualifiedName,
        Route.Profile::class.qualifiedName -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                ) + fadeOut()
            ) {
                BottomNavBar(
                    navController = navController,
                    savedJobsCount = savedJobsCount
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.Home,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                )
            }
        ) {
            // Home Screen
            composable<Route.Home> {
                HomeScreen(
                    onJobClick = { jobId ->
                        navController.navigate(Route.JobDetails(jobId))
                    },
                    onSearchClick = {
                        navController.navigate(Route.Search)
                    }
                )
            }

            // Search Screen
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

            // Profile Screen
            composable<Route.Profile> {
                ProfileScreen(
                    onSettingsClick = {
                        navController.navigate(Route.Settings)
                    }
                )
            }

            // Job Details Screen
            composable<Route.JobDetails> { backStackEntry ->
                val jobDetails: Route.JobDetails = backStackEntry.toRoute()
                JobDetailsScreen(
                    jobId = jobDetails.jobId,
                    onBackClick = { navController.navigateUp() }
                )
            }

            // Settings Screen
            composable<Route.Settings> {
                SettingsScreen(
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}
