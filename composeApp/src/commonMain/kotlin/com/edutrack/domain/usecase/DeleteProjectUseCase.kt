package com.edutrack.domain.usecase

import com.edutrack.data.repository.project.ProjectRepository

class DeleteProjectUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(id: String) {
        repository.delete(id)
    }
}
