package com.edutrack.domain.usecase

import com.edutrack.data.repository.exam.ExamRepository

class DeleteExamUseCase(
    private val repository: ExamRepository
) {
    suspend operator fun invoke(id: String) {
        repository.delete(id)
    }
}
