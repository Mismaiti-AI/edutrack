package com.edutrack.presentation.projectmanager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import com.edutrack.core.presentation.UiState
import com.edutrack.core.presentation.components.DetailCard
import com.edutrack.core.presentation.components.DetailRow
import com.edutrack.core.presentation.components.ErrorContent
import com.edutrack.core.presentation.components.ConfirmDialog
import com.edutrack.core.presentation.screens.GenericDetailScreen
import com.edutrack.core.presentation.formatDisplay
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ProjectDetailScreen(
    itemId: String,
    viewModel: ProjectDetailViewModel = koinViewModel { parametersOf(itemId) },
    onBackClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    when (val state = uiState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        is UiState.Success -> {
            ConfirmDialog(
                show = showDeleteDialog,
                title = "Delete Project",
                message = "Are you sure you want to delete this project manager? This action cannot be undone.",
                confirmText = "Delete",
                isDestructive = true,
                onConfirm = {
                    showDeleteDialog = false
                    viewModel.deleteItem(onSuccess = onBackClick)
                },
                onDismiss = { showDeleteDialog = false }
            )

            GenericDetailScreen(
                title = "Project Details",
                item = state.data,
                onBackClick = onBackClick,
                onDeleteClick = { showDeleteDialog = true },
            ) { item ->
                DetailCard(
                    title = "Details",
                    rows = listOf(
                        DetailRow(
                            label = "Title",
                            value = item.title.toString()
                        ),
                        DetailRow(
                            label = "Subject",
                            value = item.subject.toString()
                        ),
                        DetailRow(
                            label = "Start Date",
                            value = item.startDate.formatDisplay()
                        ),
                        DetailRow(
                            label = "Due Date",
                            value = item.dueDate.formatDisplay()
                        ),
                        DetailRow(
                            label = "Description",
                            value = item.description.toString()
                        ),
                        DetailRow(
                            label = "Milestones",
                            value = item.milestones.toString()
                        ),
                        DetailRow(
                            label = "Attachment Url",
                            value = item.attachmentUrl.toString()
                        ),
                    )
                )
            }
        }
        is UiState.Error -> {
            ErrorContent(
                title = "Error",
                message = state.message,
                onRetry = { viewModel.loadDetail() }
            )
        }
    }
}
