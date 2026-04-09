package com.edutrack.presentation.assignmenttracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
fun AssignmentListScreen(
    viewModel: AssignmentListViewModel = koinViewModel(),
    onItemClick: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Assignment Tracking",
                        fontWeight = FontWeight.ExtraBold,
                    )
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
                val filteredItems = state.data

                Column(
                    modifier = Modifier.fillMaxSize().padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding(),
                    ),
                ) {
                    // ── Search bar ──
                    SearchBar(
                        query = "",
                        onQueryChange = { },
                        onSearch = { },
                        placeholder = { Text("Search...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        active = false,
                        onActiveChange = { },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {}

                    // ── Status filter chips ──

                    // ── Filter chips ──

                    // ── List layout ──
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    ) {
                        if (filteredItems.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxHeight(0.5f).fillMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    EmptyStateContent(
                                        icon = Icons.Default.CheckCircle,
                                        message = "No assignments yet",
                                    )
                                }
                            }
                        } else {
                            items(filteredItems, key = { it.id }) { item ->
                                AppListTile(
                                    leadingContent = {
                                        IconBox(icon = Icons.Default.CheckCircle)
                                    },
                                    title = item.title.toString(),
                                    subtitle = item.subject.toString(),
                                    onClick = { onItemClick(item.id) },
                                )
                            }
                        }
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
