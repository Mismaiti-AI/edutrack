package com.edutrack

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.edutrack.core.presentation.navigation.AppOrchestrator
import com.edutrack.core.presentation.navigation.AppState
import com.edutrack.core.presentation.navigation.NavigationTab
import com.edutrack.presentation.gsheets.GSheetSetupScreen
import com.edutrack.core.data.gsheets.GoogleSheetsConfig
import org.koin.compose.koinInject
import com.edutrack.presentation.navigation.AppRoutes
import com.edutrack.presentation.settings.SettingsScreen
import com.edutrack.presentation.profile.ProfileScreen
import com.edutrack.presentation.dashboard.DashboardScreen
import com.edutrack.presentation.assignmenttracking.AssignmentListScreen
import com.edutrack.presentation.assignmenttracking.AssignmentDetailScreen
import com.edutrack.presentation.examtracking.ExamListScreen
import com.edutrack.presentation.examtracking.ExamDetailScreen
import com.edutrack.presentation.projecttracking.ProjectListScreen
import com.edutrack.presentation.projecttracking.ProjectDetailScreen

@Composable
fun AppContent() {
    val sheetsConfig = koinInject<GoogleSheetsConfig>()
    val gsheetsStartRoute = remember {
        if (sheetsConfig.isConfigured) AppRoutes.Dashboard
        else AppRoutes.GSheetSetup
    }

    AppOrchestrator(
        showTopBar = false,
        appState = AppState.Home,
        tabs = listOf(
            NavigationTab(
                route = AppRoutes.Dashboard,
                title = "Home",
                icon = Icons.Default.Home
            ),
            NavigationTab(
                route = AppRoutes.AssignmentList,
                title = "Assignments",
                icon = Icons.Default.Home
            ),
            NavigationTab(
                route = AppRoutes.ExamList,
                title = "Exams",
                icon = Icons.Default.Home
            ),
            NavigationTab(
                route = AppRoutes.Profile,
                title = "Profile",
                icon = Icons.Default.Person
            ),
        ),
        homeStartDestination = gsheetsStartRoute,
        homeBuilder = { navController ->
            composable<AppRoutes.Dashboard> {
                DashboardScreen(
                    onAssignmentListClick = { navController.navigate(AppRoutes.AssignmentList) },
                    onExamListClick = { navController.navigate(AppRoutes.ExamList) },
                    onSettingsClick = { navController.navigate(AppRoutes.Settings) },
                    onRecentItemClick = { id -> navController.navigate(AppRoutes.AssignmentDetail(itemId = id)) }
                )
            }
            composable<AppRoutes.AssignmentList> {
                AssignmentListScreen(
                    onItemClick = { id -> navController.navigate(AppRoutes.AssignmentDetail(itemId = id)) },
                )
            }
            composable<AppRoutes.AssignmentDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoutes.AssignmentDetail>()
                AssignmentDetailScreen(
                    itemId = route.itemId,
                    onBackClick = { navController.popBackStack() },
                )
            }
            composable<AppRoutes.ExamList> {
                ExamListScreen(
                    onItemClick = { id -> navController.navigate(AppRoutes.ExamDetail(itemId = id)) },
                )
            }
            composable<AppRoutes.ExamDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoutes.ExamDetail>()
                ExamDetailScreen(
                    itemId = route.itemId,
                    onBackClick = { navController.popBackStack() },
                )
            }
            composable<AppRoutes.ProjectList> {
                ProjectListScreen(
                    onItemClick = { id -> navController.navigate(AppRoutes.ProjectDetail(itemId = id)) },
                )
            }
            composable<AppRoutes.ProjectDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoutes.ProjectDetail>()
                ProjectDetailScreen(
                    itemId = route.itemId,
                    onBackClick = { navController.popBackStack() },
                )
            }
            composable<AppRoutes.GSheetSetup> {
                GSheetSetupScreen(
                    onSetupComplete = {
                        navController.navigate(AppRoutes.Dashboard) {
                            popUpTo(AppRoutes.GSheetSetup) { inclusive = true }
                        }
                    }
                )
            }
            composable<AppRoutes.Settings> {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }
            composable<AppRoutes.Profile> {
                ProfileScreen(
                    onSettingsClick = { navController.navigate(AppRoutes.Settings) },
                )
            }
        }
    )
}
