package com.edutrack.domain.usecase

import com.edutrack.data.repository.assignment.AssignmentRepository

class DeleteAssignmentUseCase(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(id: String) {
        repository.delete(id)
    }
}
