package com.edutrack.core.presentation.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
// Route definitions are generated in AppRoutes.kt (from app_routes.kt.j2)

// ============================================================
// MainScaffold - App Shell with Bottom Nav
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    tabs: List<NavigationTab>,
    startDestination: Any,
    showTopBar: Boolean = true,
    builder: androidx.navigation.NavGraphBuilder.(NavHostController) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    val isOnTab = tabs.any { tab ->
        currentDestination?.hasRoute(tab.route::class) == true
    }

    val currentTabTitle = tabs.find { tab ->
        currentDestination?.hasRoute(tab.route::class) == true
    }?.title ?: "App"

    Scaffold(
        topBar = {
            if (showTopBar && isOnTab) {
                TopAppBar(
                    title = { Text(currentTabTitle) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        },
        bottomBar = {
            if (isOnTab) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title) },
                            selected = currentDestination?.hierarchy?.any {
                                it.hasRoute(tab.route::class)
                            } == true,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            builder(navController)
        }
    }
}

// ============================================================
// AppNavigationGraph - Standalone Flows (Auth, etc.)
// ============================================================

@Composable
fun AppNavigationGraph(
    startDestination: Any,
    builder: androidx.navigation.NavGraphBuilder.(NavHostController) -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        builder(navController)
    }
}

// ============================================================
// Data Classes
// ============================================================

data class NavigationTab(
    val route: Any,
    val title: String,
    val icon: ImageVector,
    val visibleTo: List<String> = emptyList()  // empty = visible to all roles
)
