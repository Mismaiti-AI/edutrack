package com.edutrack.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import com.edutrack.domain.model.Exam
import com.edutrack.data.repository.exam.ExamRepository

class GetExamDetailUseCase(
    private val repository: ExamRepository
) {
    operator fun invoke(): StateFlow<Exam?> = repository.selectedItem

    suspend fun load(id: String) {
        repository.selectItem(id)
    }
}
