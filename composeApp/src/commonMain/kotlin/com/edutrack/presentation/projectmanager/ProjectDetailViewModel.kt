package com.edutrack.presentation.projectmanager

import com.edutrack.core.presentation.BaseViewModel
import com.edutrack.core.presentation.UiState
import com.edutrack.domain.model.Project
import com.edutrack.domain.usecase.GetProjectDetailUseCase
import com.edutrack.domain.usecase.DeleteProjectUseCase
import kotlinx.coroutines.flow.StateFlow

class ProjectDetailViewModel(
    private val itemId: String,
    private val getDetailUseCase: GetProjectDetailUseCase,
    private val deleteUseCase: DeleteProjectUseCase,
) : BaseViewModel() {

    val uiState: StateFlow<UiState<Project?>> = uiStateFrom(getDetailUseCase())

    init { loadDetail() }

    fun loadDetail() = safeLaunch { getDetailUseCase.load(itemId) }

    fun deleteItem(onSuccess: () -> Unit = {}) = safeLaunch {
        deleteUseCase(itemId)
        onSuccess()
    }
}
