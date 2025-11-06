package com.example.jobfinderapp.presentation.navigation


import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val badge: Int? = null
)

@Composable
fun BottomNavBar(
    navController: NavController,
    savedJobsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = remember(savedJobsCount) {
        listOf(
            BottomNavItem(
                route = Route.Home,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                label = "Home"
            ),
            BottomNavItem(
                route = Route.Search,
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.Search,
                label = "Search"
            ),
            BottomNavItem(
                route = Route.Saved,
                selectedIcon = Icons.Filled.Bookmark,
                unselectedIcon = Icons.Outlined.BookmarkBorder,
                label = "Saved",
                badge = if (savedJobsCount > 0) savedJobsCount else null
            ),
            BottomNavItem(
                route = Route.Profile,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                label = "Profile"
            )
        )
    }

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute?.contains(item.route::class.simpleName ?: "") == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(Route.Home) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badge != null) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error
                                ) {
                                    Text(
                                        text = item.badge.toString(),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}
