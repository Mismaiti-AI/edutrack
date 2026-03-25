package com.edutrack.presentation.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel
import com.edutrack.core.presentation.UiState
import com.edutrack.core.presentation.components.ErrorContent
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.screens.DashboardStat
import com.edutrack.core.presentation.screens.GenericDashboardScreen
import com.edutrack.core.presentation.screens.QuickAction

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onSettingsClick: () -> Unit = {},
    onRecentItemClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        is UiState.Success -> {
            val overview = state.data
            GenericDashboardScreen(
                title = "EduTrack",
                greeting = "Welcome back",
                stats = listOf(
                    DashboardStat(
                        label = "Total Assignments",
                        value = overview.total_assignments.toString(),
                        icon = Icons.Default.Check,
                    ),
                    DashboardStat(
                        label = "Upcoming Exams",
                        value = overview.upcoming_exams.toString(),
                        icon = Icons.Default.DateRange,
                    ),
                    DashboardStat(
                        label = "Active Projects",
                        value = overview.active_projects.toString(),
                        icon = Icons.Default.Build,
                    ),
                ),
                quickActions = listOf(
                ),
                onSettingsClick = onSettingsClick,
                recentItems = overview.recentAssignments,
                recentTitle = "Recent Assignments",
                onRecentItemClick = { onRecentItemClick(it.id) },
                recentItemContent = { item ->
                    ListItemCard(
                        title = item.title.toString(),
                        subtitle = item.subject.toString(),
                        showChevron = true,
                    )
                },
            )
        }
        is UiState.Error -> {
            ErrorContent(
                title = "Error",
                message = state.message,
                onRetry = { viewModel.refresh() }
            )
        }
    }
}
