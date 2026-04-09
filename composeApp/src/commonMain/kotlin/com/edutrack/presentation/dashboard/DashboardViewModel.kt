package com.edutrack.presentation.dashboard

import com.edutrack.core.presentation.BaseViewModel
import com.edutrack.core.presentation.UiState
import com.edutrack.domain.usecase.DashboardOverview
import com.edutrack.domain.usecase.GetDashboardOverviewUseCase
import kotlinx.coroutines.flow.StateFlow

class DashboardViewModel(
    private val getDashboardUseCase: GetDashboardOverviewUseCase
) : BaseViewModel() {

    val uiState: StateFlow<UiState<DashboardOverview>> = uiStateFrom(getDashboardUseCase())

    init { loadDashboard() }

    fun loadDashboard() = safeLaunch { getDashboardUseCase.load() }

    fun refresh() = safeLaunch { getDashboardUseCase.refresh() }
}
