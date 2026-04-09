package com.edutrack.presentation.assignmenttracking

import com.edutrack.core.presentation.BaseViewModel
import com.edutrack.core.presentation.UiState
import com.edutrack.domain.model.Assignment
import com.edutrack.domain.usecase.GetAssignmentListUseCase
import kotlinx.coroutines.flow.StateFlow

class AssignmentListViewModel(
    private val getListUseCase: GetAssignmentListUseCase
) : BaseViewModel() {

    val uiState: StateFlow<UiState<List<Assignment>>> = uiStateFrom(getListUseCase())

    init { loadItems() }

    fun loadItems() = safeLaunch { getListUseCase.load() }

    fun refresh() = safeLaunch { getListUseCase.refresh() }
}
