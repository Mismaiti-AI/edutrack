package com.edutrack.presentation.examplanner

import com.edutrack.core.presentation.BaseViewModel
import com.edutrack.core.presentation.UiState
import com.edutrack.domain.model.Exam
import com.edutrack.domain.usecase.GetExamDetailUseCase
import com.edutrack.domain.usecase.DeleteExamUseCase
import kotlinx.coroutines.flow.StateFlow

class ExamDetailViewModel(
    private val itemId: String,
    private val getDetailUseCase: GetExamDetailUseCase,
    private val deleteUseCase: DeleteExamUseCase,
) : BaseViewModel() {

    val uiState: StateFlow<UiState<Exam?>> = uiStateFrom(getDetailUseCase())

    init { loadDetail() }

    fun loadDetail() = safeLaunch { getDetailUseCase.load(itemId) }

    fun deleteItem(onSuccess: () -> Unit = {}) = safeLaunch {
        deleteUseCase(itemId)
        onSuccess()
    }
}
