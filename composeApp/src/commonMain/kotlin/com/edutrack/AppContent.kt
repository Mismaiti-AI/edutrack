package com.edutrack

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.edutrack.core.presentation.navigation.AppOrchestrator
import com.edutrack.core.presentation.navigation.AppState
import com.edutrack.core.presentation.navigation.NavigationTab
import com.edutrack.core.presentation.screens.GenericSplashScreen
import com.edutrack.core.presentation.UiState
import com.edutrack.core.presentation.auth.AuthViewModel
import com.edutrack.core.presentation.auth.AuthScreenContent
import com.edutrack.core.presentation.auth.AuthSocialButton
import com.edutrack.getSocialSignInLabel
import org.koin.compose.viewmodel.koinViewModel
import com.edutrack.presentation.navigation.AppRoutes
import com.edutrack.presentation.settings.SettingsScreen
import com.edutrack.presentation.profile.ProfileScreen
import com.edutrack.presentation.dashboard.DashboardScreen
import com.edutrack.presentation.assignmenttracker.AssignmentListScreen
import com.edutrack.presentation.assignmenttracker.AssignmentDetailScreen
import com.edutrack.presentation.examplanner.ExamListScreen
import com.edutrack.presentation.examplanner.ExamDetailScreen
import com.edutrack.presentation.projectmanager.ProjectListScreen
import com.edutrack.presentation.projectmanager.ProjectDetailScreen

@Composable
fun AppContent() {
    val authViewModel: AuthViewModel = koinViewModel()
    val isLoggedIn = authViewModel.isLoggedIn
    val state by authViewModel.uiState.collectAsStateWithLifecycle()

    var splashFinished by remember { mutableStateOf(false) }
    var appContentState by remember { mutableStateOf(AppState.Splash) }

    // React to auth state changes (login success, logout)
    LaunchedEffect(state, splashFinished) {
        when (val st = state) {
            is UiState.Success -> {
                appContentState = AppState.Home
            }
            is UiState.Error -> {
                if (st.message == "Logged out") {
                    appContentState = AppState.Auth
                }
            }
            else -> Unit
        }

        if (splashFinished) {
            appContentState = if (isLoggedIn) {
                AppState.Home
            } else {
                AppState.Auth
            }
        }
    }

    AppOrchestrator(
        showTopBar = false,
        authStartDestination = AppRoutes.Login,
        appState = appContentState,
        splashContent = {
            GenericSplashScreen(
                appName = "EduTrack",
                onFinished = {
                    splashFinished = true
                }
            )
        },
        authBuilder = { navController ->
            composable<AppRoutes.Login> {
                AuthScreenContent(
                    socialButtons = listOf(
                        AuthSocialButton(getSocialSignInLabel())
                    ),
                    authViewModel = authViewModel,
                    onLoginSuccess = { }
                )
            }
        },
        tabs = listOf(
            NavigationTab(
                route = AppRoutes.Dashboard,
                title = "Home",
                icon = Icons.Default.Home
            ),
            NavigationTab(
                route = AppRoutes.Settings,
                title = "Profile",
                icon = Icons.Default.AccountCircle
            ),
        ),
        homeStartDestination = AppRoutes.Dashboard,
        homeBuilder = { navController ->
            composable<AppRoutes.Dashboard> {
                DashboardScreen(
                    onSettingsClick = { navController.navigate(AppRoutes.Settings) },
                    onRecentItemClick = { id -> navController.navigate(AppRoutes.AssignmentDetail(itemId = id)) }
                )
            }
            composable<AppRoutes.AssignmentList> {
                AssignmentListScreen(
                    onItemClick = { id -> navController.navigate(AppRoutes.AssignmentDetail(itemId = id)) },
                    onAddClick = { }
                )
            }
            composable<AppRoutes.AssignmentDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoutes.AssignmentDetail>()
                AssignmentDetailScreen(
                    itemId = route.itemId,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { }
                )
            }
            composable<AppRoutes.ExamList> {
                ExamListScreen(
                    onItemClick = { id -> navController.navigate(AppRoutes.ExamDetail(itemId = id)) },
                    onAddClick = { }
                )
            }
            composable<AppRoutes.ExamDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoutes.ExamDetail>()
                ExamDetailScreen(
                    itemId = route.itemId,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { }
                )
            }
            composable<AppRoutes.ProjectList> {
                ProjectListScreen(
                    onItemClick = { id -> navController.navigate(AppRoutes.ProjectDetail(itemId = id)) },
                    onAddClick = { }
                )
            }
            composable<AppRoutes.ProjectDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoutes.ProjectDetail>()
                ProjectDetailScreen(
                    itemId = route.itemId,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { }
                )
            }
            composable<AppRoutes.Settings> {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() },
                    onLogoutClick = {
                        authViewModel.logout()
                    },
                )
            }
            composable<AppRoutes.Profile> {
                ProfileScreen(
                    onSettingsClick = { navController.navigate(AppRoutes.Settings) },
                    onLogoutClick = {
                        authViewModel.logout()
                    },
                )
            }
        }
    )
}
