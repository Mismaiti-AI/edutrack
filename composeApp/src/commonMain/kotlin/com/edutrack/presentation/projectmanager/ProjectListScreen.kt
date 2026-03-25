package com.edutrack.presentation.projectmanager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel
import com.edutrack.core.presentation.UiState
import com.edutrack.core.presentation.components.ErrorContent
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.screens.GenericListScreen
import com.edutrack.core.presentation.components.StatusBadge

@Composable
fun ProjectListScreen(
    viewModel: ProjectListViewModel = koinViewModel(),
    onItemClick: (String) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        is UiState.Success -> {
            GenericListScreen(
                title = "Project Manager",
                items = state.data,
                emptyMessage = "No project manager yet",
                onItemClick = { onItemClick(it.id) },
                onAddClick = onAddClick,
                onRefresh = { viewModel.refresh() },
                searchEnabled = true,
                showFab = false,
            ) { item ->
                ListItemCard(
                    title = item.title.toString(),
                    subtitle = item.subject.toString(),
                    showChevron = true,
                    trailingContent = { StatusBadge(text = item.dueDate.toString()) }
                )
            }
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
