package com.edutrack.presentation.examplanner

import com.edutrack.core.presentation.BaseViewModel
import com.edutrack.core.presentation.UiState
import com.edutrack.domain.model.Exam
import com.edutrack.domain.usecase.GetExamListUseCase
import kotlinx.coroutines.flow.StateFlow

class ExamListViewModel(
    private val getListUseCase: GetExamListUseCase
) : BaseViewModel() {

    val uiState: StateFlow<UiState<List<Exam>>> = uiStateFrom(getListUseCase())

    init { loadItems() }

    fun loadItems() = safeLaunch { getListUseCase.load() }

    fun refresh() = safeLaunch { getListUseCase.refresh() }
}
