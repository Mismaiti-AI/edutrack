package com.edutrack.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

@Composable
fun AppOrchestrator(
    appState: AppState,
    // Splash phase
    splashContent: (@Composable () -> Unit)? = null,
    // Onboarding phase
    onboardingContent: (@Composable () -> Unit)? = null,
    // Auth phase
    authStartDestination: Any = Unit,
    // Setup Content
    setupContent: (@Composable () -> Unit)? = null,
    authBuilder: (NavGraphBuilder.(NavHostController) -> Unit)? = null,
    // Home phase (MainScaffold)
    tabs: List<NavigationTab> = emptyList(),
    homeStartDestination: Any = Unit,
    homeBuilder: (NavGraphBuilder.(NavHostController) -> Unit)? = null,
    showTopBar: Boolean = true,
) {
    when (appState) {
        AppState.Splash -> {
            splashContent?.invoke()
        }

        AppState.Onboarding -> {
            onboardingContent?.invoke()
        }

        AppState.Setup -> {
            setupContent?.invoke()
        }

        AppState.Auth -> {
            if (authBuilder != null) {
                AppNavigationGraph(
                    startDestination = authStartDestination,
                    builder = authBuilder
                )
            }
        }

        AppState.Home -> {
            if (tabs.isNotEmpty() && homeBuilder != null) {
                MainScaffold(
                    tabs = tabs,
                    startDestination = homeStartDestination,
                    showTopBar = showTopBar,
                    builder = homeBuilder
                )
            }
        }
    }
}

enum class AppState {
    Splash,
    Onboarding,
    Setup,
    Auth,
    Home
}
