package com.edutrack.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import com.edutrack.domain.model.Assignment
import com.edutrack.data.repository.assignment.AssignmentRepository

class GetAssignmentListUseCase(
    private val repository: AssignmentRepository
) {
    operator fun invoke(): StateFlow<List<Assignment>> = repository.items

    suspend fun load() {
        repository.loadAll()
    }

    suspend fun refresh() {
        repository.loadAll()
    }
}
