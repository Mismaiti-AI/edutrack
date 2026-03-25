package com.edutrack.presentation.assignmenttracker

import com.edutrack.core.presentation.BaseViewModel
import com.edutrack.core.presentation.UiState
import com.edutrack.domain.model.Assignment
import com.edutrack.domain.usecase.GetAssignmentDetailUseCase
import com.edutrack.domain.usecase.DeleteAssignmentUseCase
import kotlinx.coroutines.flow.StateFlow

class AssignmentDetailViewModel(
    private val itemId: String,
    private val getDetailUseCase: GetAssignmentDetailUseCase,
    private val deleteUseCase: DeleteAssignmentUseCase,
) : BaseViewModel() {

    val uiState: StateFlow<UiState<Assignment?>> = uiStateFrom(getDetailUseCase())

    init { loadDetail() }

    fun loadDetail() = safeLaunch { getDetailUseCase.load(itemId) }

    fun deleteItem(onSuccess: () -> Unit = {}) = safeLaunch {
        deleteUseCase(itemId)
        onSuccess()
    }
}
