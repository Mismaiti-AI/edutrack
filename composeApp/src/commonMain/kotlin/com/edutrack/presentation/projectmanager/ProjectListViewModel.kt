package com.edutrack.presentation.projectmanager

import com.edutrack.core.presentation.BaseViewModel
import com.edutrack.core.presentation.UiState
import com.edutrack.domain.model.Project
import com.edutrack.domain.usecase.GetProjectListUseCase
import kotlinx.coroutines.flow.StateFlow

class ProjectListViewModel(
    private val getListUseCase: GetProjectListUseCase
) : BaseViewModel() {

    val uiState: StateFlow<UiState<List<Project>>> = uiStateFrom(getListUseCase())

    init { loadItems() }

    fun loadItems() = safeLaunch { getListUseCase.load() }

    fun refresh() = safeLaunch { getListUseCase.refresh() }
}
