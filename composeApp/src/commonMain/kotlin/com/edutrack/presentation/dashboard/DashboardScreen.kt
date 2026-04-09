package com.edutrack.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.edutrack.core.presentation.UiState
import com.edutrack.core.presentation.components.ErrorContent
import com.edutrack.ui.atoms.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onAssignmentListClick: () -> Unit = {},
    onExamListClick: () -> Unit = {},
    onProjectListClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onRecentItemClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "EduTrack",
                        fontWeight = FontWeight.ExtraBold,
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                },
            )
        },
    ) { padding ->

        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                val overview = state.data

                LazyColumn(
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = padding.calculateBottomPadding() + 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // ── Hero Block ──
                    item {
                        HeroBlock(
                            title = "Welcome",
                            subtitle = "Here's your EduTrack overview",
                        )
                    }

                    // ── Stats Row ──
                    item {
                        StatRow(
                            stats = listOf(
                                StatItem(
                                    value = overview.assignments.toString(),
                                    label = "Assignments",
                                ),
                                StatItem(
                                    value = overview.exams.toString(),
                                    label = "Exams",
                                ),
                                StatItem(
                                    value = overview.projects.toString(),
                                    label = "Projects",
                                ),
                            ),
                        )
                    }

                    // ── Quick Actions ──
                    item {
                        Text(
                            "Quick Actions",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            FilledTonalButton(
                                onClick = onAssignmentListClick,
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.large,
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Assignments",
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Assignments", fontWeight = FontWeight.SemiBold)
                            }
                            FilledTonalButton(
                                onClick = onExamListClick,
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.large,
                            ) {
                                Icon(
                                    Icons.Default.Article,
                                    contentDescription = "Exams",
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Exams", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // ── Feature Cards ──
                    item {
                        Text(
                            "Features",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    item {
                        AppListTile(
                            leadingContent = {
                                IconBox(icon = Icons.Default.CheckCircle)
                            },
                            title = "Assignments",
                            subtitle = "Browse Assignments",
                            onClick = { onAssignmentListClick() },
                            trailingContent = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp),
                                )
                            },
                        )
                    }
                    item {
                        AppListTile(
                            leadingContent = {
                                IconBox(icon = Icons.Default.Article)
                            },
                            title = "Exams",
                            subtitle = "Browse Exams",
                            onClick = { onExamListClick() },
                            trailingContent = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp),
                                )
                            },
                        )
                    }
                    item {
                        AppListTile(
                            leadingContent = {
                                IconBox(icon = Icons.Default.Build)
                            },
                            title = "Projects",
                            subtitle = "Browse Projects",
                            onClick = { onProjectListClick() },
                            trailingContent = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp),
                                )
                            },
                        )
                    }

                    // ── Recent Items ──
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "Recent Assignments",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            TextButton(onClick = { /* See all */ }) {
                                Text("See all", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                    items(
                        items = overview.recentAssignments,
                        key = { it.id },
                    ) { item ->
                        AppListTile(
                            leadingContent = {
                                IconBox(icon = Icons.Default.CheckCircle)
                            },
                            title = item.title.toString(),
                            subtitle = item.subject.toString(),
                            onClick = { onRecentItemClick(item.id) },
                        )
                    }
                }
            }

            is UiState.Error -> {
                ErrorContent(
                    title = "Error",
                    message = state.message,
                    onRetry = { viewModel.refresh() },
                )
            }
        }
    }
}
