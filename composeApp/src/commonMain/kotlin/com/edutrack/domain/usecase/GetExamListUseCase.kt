package com.edutrack.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import com.edutrack.domain.model.Exam
import com.edutrack.data.repository.exam.ExamRepository

class GetExamListUseCase(
    private val repository: ExamRepository
) {
    operator fun invoke(): StateFlow<List<Exam>> = repository.items

    suspend fun load() {
        repository.loadAll()
    }

    suspend fun refresh() {
        repository.loadAll()
    }
}
