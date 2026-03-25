package com.edutrack.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import com.edutrack.domain.model.Assignment
import com.edutrack.data.repository.assignment.AssignmentRepository

class GetAssignmentDetailUseCase(
    private val repository: AssignmentRepository
) {
    operator fun invoke(): StateFlow<Assignment?> = repository.selectedItem

    suspend fun load(id: String) {
        repository.selectItem(id)
    }
}
